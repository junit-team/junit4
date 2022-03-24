package junit.framework;

import org.junit.Ignore;
import org.junit.runner.Description;

public class DescriptionEditor{
    private boolean isIgnored(Description description) {
        return description.getAnnotation(Ignore.class) != null;
    }

    public Description removeIgnored(Description description) {
        if (isIgnored(description)) {
            return Description.EMPTY;
        }
        Description result = description.childlessCopy();
        for (Description each : description.getChildren()) {
            Description child = removeIgnored(each);
            if (!child.isEmpty()) {
                result.addChild(child);
            }
        }
        return result;
    }
}