package diff;

public class Binary {

    private String contentType;
    private String suffix;
    private String base64;

    public Binary() {}

    public Binary(String contentType, String suffix, String base64) {
        this.contentType = contentType;
        this.suffix = suffix;
        this.base64 = base64;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getBase64() {
        return base64;
    }

    @Override
    public String toString() {
        return "Binary{" +
                "contentType='" + contentType + '\'' +
                ", suffix='" + suffix + '\'' +
                ", base64='" + base64 + '\'' +
                '}';
    }
}

