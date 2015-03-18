package fr.lassiergedeon.dontbreakthechain.model;

/**
 * Created by Antoine on 18/03/2015.
 */
public class Chain {
    private int id;
    private int idTask;
    private String firstDate;
    private String lastDate;

    public Chain(int id, int idTask, String firstDate) {
        this.id = id;
        this.idTask = idTask;
        this.firstDate = firstDate;
        this.lastDate = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public String toString() {
        return "Chain{" +
                "id=" + id +
                ", idTask=" + idTask +
                ", firstDate='" + firstDate + '\'' +
                ", lastDate='" + lastDate + '\'' +
                '}';
    }
}
