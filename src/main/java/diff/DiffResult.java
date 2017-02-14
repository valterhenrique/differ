package diff;

class DiffResult {

    private Long offset;
    private Long length;

    public DiffResult(Long offset, Long length) {
        this.offset = offset;
        this.length = length;
    }

    public Long getOffset() {
        return offset;
    }

    public Long getLength() {
        return length;
    }
}
