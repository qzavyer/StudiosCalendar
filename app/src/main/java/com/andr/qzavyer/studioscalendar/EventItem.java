package com.andr.qzavyer.studioscalendar;

import java.util.Date;

/**
 Событие календаря
*/
public class EventItem {
    private long id;
    private long studioId;
    private String name;
    private Date start;
    private Date end;
    private int status;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getStudioId(){
        return studioId;
    }

    public void setStudioId(long id){
        this.studioId = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
