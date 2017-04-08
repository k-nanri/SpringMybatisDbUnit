package mng.doc.dao;

import java.lang.reflect.Field;
import java.util.Date;

public class Document {

    private Integer id;
    private String name;
    private String registrant;
    private Date registrationtime;
    private String editor;
    private Date edittime;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegistrant() {
        return registrant;
    }

    public String getEditor() {
        return editor;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistrant(String registrant) {
        this.registrant = registrant;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Date getRegistrationtime() {
        return registrationtime;
    }

    public Date getEdittime() {
        return edittime;
    }

    public void setRegistrationtime(Date registrationtime) {
        this.registrationtime = registrationtime;
    }

    public void setEdittime(Date edittime) {
        this.edittime = edittime;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Document [");
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {

            Field field = fields[i];
            String name = field.getName();
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            sb.append(name + " = " + value);
            if (i != (fields.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
