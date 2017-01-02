package org.junit.runner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

class NameBasedDescriptionBuilder extends DescriptionBuilder {

    NameBasedDescriptionBuilder(String displayName) {
        super.displayName = displayName;
        super.uniqueId = displayName;
        super.annotations = new ArrayList<Annotation>();
    }

    @Override
    Class<?> getTestClass() {
        return null;
    }
}
