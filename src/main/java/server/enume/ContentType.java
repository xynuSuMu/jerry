package server.enume;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-27 16:15
 */
public interface ContentType {
    enum Type {
        JSON("application/json"),
        FORM_DATA("multipart/form-data"),
        FORM("application/x-www-form-urlencoded"),
        ;
        private String typeName;

        Type(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }
}
