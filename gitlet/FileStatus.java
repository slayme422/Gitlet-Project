package gitlet;

public enum FileStatus {
        ADD("文件已添加"),
        REMOVE("文件已删除"),
        COMMIT("已提交");

        private final String descriptionl;

         FileStatus(String descriptionl) {
            this.descriptionl = descriptionl;
        }

        public String getDescription() {
            return descriptionl;
        }
}
