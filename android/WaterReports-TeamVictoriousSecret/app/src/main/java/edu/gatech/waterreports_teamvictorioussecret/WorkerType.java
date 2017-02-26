package edu.gatech.waterreports_teamvictorioussecret;

/**
 * Created by dhruvmehra on 2/26/17.
 */

public enum WorkerType {
    USER,
    WORKER,
    MANAGER,
    ADMIN;

    @Override
    public String toString() {
        return super.toString();
    }

    public static WorkerType createFromString(String worker) {
        if (worker.equals("ADMIN")) {
            return ADMIN;
        } else if (worker.equals("MANAGER")) {
            return MANAGER;
        } else if (worker.equals("WORKER")) {
            return WORKER;
        } else if (worker.equals("USER")) {
            return USER;
        } else {
            throw new IllegalArgumentException("Invalid worker type");
        }
    }
}
