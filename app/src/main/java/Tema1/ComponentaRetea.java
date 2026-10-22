package Tema1;

public abstract class ComponentaRetea {
    private final String id;
    private boolean statusOperational;

    protected ComponentaRetea(String id) {
        this.id = id;
        this.statusOperational = true;
    }

    public String getId() {
        return this.id;
    }

    public boolean getStatusOperational() {
        return this.statusOperational;
    }

    public void setStatusOperational(boolean statusOperational) {
        this.statusOperational = statusOperational;
    }

    @Override
    public String toString() {
        return this.id + " " + this.statusOperational;
    }

}
