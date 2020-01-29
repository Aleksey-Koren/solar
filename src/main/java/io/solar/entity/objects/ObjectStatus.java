package io.solar.entity.objects;


import org.codehaus.jackson.annotate.JsonCreator;

public enum ObjectStatus {
    in_space, attached_to, in_container, not_defined;

    @JsonCreator
    public static ObjectStatus fromString(String str) {
        if(str == null) {
            return not_defined;
        }
        switch (str) {
            case "in_space":
            case "attached_to":
            case "in_container":
                return ObjectStatus.valueOf(str);
            default:
                return not_defined;
        }
    }
}
