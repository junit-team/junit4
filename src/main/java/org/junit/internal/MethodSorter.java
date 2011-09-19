package org.junit.internal;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MethodSorter {

    /**
     * Gets declared methods of a class in a predictable order.
     * Using the JVM order is unwise since the Java platform does not
     * specify any particular order, and in fact JDK 7 returns a more or less
     * random order; well-written test code would not assume any order, but some
     * does, and a predictable failure is better than a random failure on
     * certain platforms. Uses bytecode order if available, else lexicographic.
     * @param clazz a class
     * @return same as {@link Class#getDeclaredMethods} but sorted
     * @see <a href="http://bugs.sun.com/view_bug.do?bug_id=7023180">JDK
     *       (non-)bug #7023180</a>
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        List<String> names = Collections.emptyList();
        Method[] methods = clazz.getDeclaredMethods();
        try {
            names = methodNamesAndDescriptors(clazz);
        } catch (IOException x) {
            // TODO report somehow?
        }
        final List<String> _names = names;
        Arrays.sort(methods, new Comparator<Method>() {
            @Override public int compare(Method m1, Method m2) {
                int i1 = _names.indexOf(nameAndDescriptor(m1));
                int i2 = _names.indexOf(nameAndDescriptor(m2));
                return i1 != i2 ? i1 - i2 : m1.toString().compareTo(m2.toString());
            }
        });
        return methods;
    }

    static List<String> methodNamesAndDescriptors(Class<?> clazz) throws IOException {
        ClassLoader l = clazz.getClassLoader();
        String name = clazz.getName().replace('.', '/') + ".class";
        InputStream is = l != null ? l.getResourceAsStream(name) : ClassLoader.getSystemResourceAsStream(name);
        try {
            return methodNamesAndDescriptors(is);
        } finally {
            is.close();
        }
    }

    private static List<String> methodNamesAndDescriptors(InputStream bytecode) throws IOException {
        DataInput input = new DataInputStream(bytecode);
        skip(input, 8); // magic, minor_version, major_version
        int size = input.readUnsignedShort() - 1; // constant_pool_count
        String[] utf8Strings = new String[size];
        for (int i = 0; i < size; i++) { // constant_pool
            byte tag = input.readByte();
            switch (tag) {
                case 1: // CONSTANT_Utf8
                    utf8Strings[i] = input.readUTF();
                    break;
                case 7: // CONSTANT_Class
                case 8: // CONSTANT_String
                    skip(input, 2);
                    break;
                case 3: // CONSTANT_Integer
                case 4: // CONSTANT_Float
                case 9: // CONSTANT_Fieldref
                case 10: // CONSTANT_Methodref
                case 11: // CONSTANT_InterfaceMethodref
                case 12: // CONSTANT_NameAndType
                    skip(input, 4);
                    break;
                case 5: // CONSTANT_Long
                case 6: // CONSTANT_Double
                    skip(input, 8);
                    i++; // weirdness in spec
                    break;
                default:
                    throw new IOException("Unrecognized constant pool tag " + tag + " @" + i + " after " + Arrays.asList(utf8Strings));
            }
        }
        skip(input, 6); // access_flags, this_class, super_class
        skip(input, input.readUnsignedShort() * 2); // interfaces_count, interfaces
        int fields_count = input.readUnsignedShort();
        for (int i = 0; i < fields_count; i++) { // field_info
            skip(input, 6); // access_flags, name_index, descriptor_index
            int attributes_count = input.readUnsignedShort();
            for (int j = 0; j < attributes_count; j++) { // attribute_info
                skip(input, 2); //attribute_name_index
                skip(input, input.readInt()); // attribute_length, info
            }
        }
        int methods_count = input.readUnsignedShort();
        List<String> result = new ArrayList<String>(methods_count);
        for (int i = 0; i < methods_count; i++) { // method_info
            skip(input, 2); // access_flags
            int name_index = input.readUnsignedShort() - 1;
            if (name_index >= size || name_index < 0) {
                throw new IOException("@" + i + ": method_info.name_index " + name_index + " vs. size of pool " + size);
            }
            int descriptor_index = input.readUnsignedShort() - 1;
            if (descriptor_index >= size || descriptor_index < 0) {
                throw new IOException("@" + i + ": method_info.descriptor_index " + descriptor_index + " vs. size of pool " + size);
            }
            String name = utf8Strings[name_index];
            if (!name.equals("<init>") && !name.equals("<clinit>")) {
                result.add(name + '~' + utf8Strings[descriptor_index]);
            }
            int attributes_count = input.readUnsignedShort();
            for (int j = 0; j < attributes_count; j++) { // attribute_info
                skip(input, 2); //attribute_name_index
                skip(input, input.readInt()); // attribute_length, info
            }
        }
        return result;
    }

    private static void skip(DataInput input, int bytes) throws IOException {
        int skipped = input.skipBytes(bytes);
        if (skipped != bytes) {
            throw new IOException("Truncated class file");
        }
    }

    static String nameAndDescriptor(Method m) {
        StringBuilder b = new StringBuilder(m.getName());
        b.append('~');
        b.append('(');
        for (Class<?> c : m.getParameterTypes()) {
            binaryName(c, b);
        }
        b.append(')');
        binaryName(m.getReturnType(), b);
        return b.toString();
    }

    private static void binaryName(Class<?> c, StringBuilder b) {
        if (c == Void.TYPE) {
            b.append('V');
        } else if (c == Byte.TYPE) {
            b.append('B');
        } else if (c == Character.TYPE) {
            b.append('C');
        } else if (c == Double.TYPE) {
            b.append('D');
        } else if (c == Float.TYPE) {
            b.append('F');
        } else if (c == Integer.TYPE) {
            b.append('I');
        } else if (c == Long.TYPE) {
            b.append('J');
        } else if (c == Short.TYPE) {
            b.append('S');
        } else if (c == Boolean.TYPE) {
            b.append('Z');
        } else if (c.isArray()) {
            b.append('[');
            binaryName(c.getComponentType(), b);
        } else {
            b.append('L');
            b.append(c.getName().replace('.', '/'));
            b.append(';');
        }
    }

    private MethodSorter() {}

}
