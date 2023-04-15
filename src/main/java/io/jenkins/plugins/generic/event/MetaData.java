package io.jenkins.plugins.generic.event;

public class MetaData {

    private String oldName;

    private String newName;

    private String oldUrl;

    private String newUrl;

    public MetaData(MetaDataBuilder builder) {
        this.oldName = builder.oldName;
        this.newName = builder.newName;
        this.oldUrl = builder.oldUrl;
        this.newUrl = builder.newUrl;
    }

    public static class MetaDataBuilder {
        private String oldName;

        private String newName;

        private String oldUrl;

        private String newUrl;

        public MetaDataBuilder() {

        }

        public MetaDataBuilder oldName(String oldName) {
            this.oldName = oldName;
            return this;
        }

        public MetaDataBuilder newName(String newName) {
            this.newName = newName;
            return this;
        }

        public MetaDataBuilder oldUrl(String oldUrl) {
            this.oldUrl = oldUrl;
            return this;
        }

        public MetaDataBuilder newUrl(String newUrl) {
            this.newUrl = newUrl;
            return this;
        }

        public MetaData build() {
            return new MetaData(this);
        }
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getOldUrl() {
        return oldUrl;
    }

    public void setOldUrl(String oldUrl) {
        this.oldUrl = oldUrl;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    @Override
    public String toString() {
        return "MetaData [oldUrl=" + oldUrl + ", newUrl=" + newUrl +
                ", oldName=" + oldName + ", newName=" + newName + "]";
    }
}
