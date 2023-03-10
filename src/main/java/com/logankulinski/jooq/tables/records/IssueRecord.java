/*
 * This file is generated by jOOQ.
 */
package com.logankulinski.jooq.tables.records;


import com.logankulinski.jooq.tables.Issue;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IssueRecord extends UpdatableRecordImpl<IssueRecord> implements Record4<Integer, String, String, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.issue.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.issue.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.issue.title</code>.
     */
    public void setTitle(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.issue.title</code>.
     */
    public String getTitle() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.issue.url</code>.
     */
    public void setUrl(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.issue.url</code>.
     */
    public String getUrl() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.issue.notification_date</code>.
     */
    public void setNotificationDate(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.issue.notification_date</code>.
     */
    public LocalDateTime getNotificationDate() {
        return (LocalDateTime) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, LocalDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, String, String, LocalDateTime> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Issue.ISSUE.ID;
    }

    @Override
    public Field<String> field2() {
        return Issue.ISSUE.TITLE;
    }

    @Override
    public Field<String> field3() {
        return Issue.ISSUE.URL;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return Issue.ISSUE.NOTIFICATION_DATE;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getTitle();
    }

    @Override
    public String component3() {
        return getUrl();
    }

    @Override
    public LocalDateTime component4() {
        return getNotificationDate();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getTitle();
    }

    @Override
    public String value3() {
        return getUrl();
    }

    @Override
    public LocalDateTime value4() {
        return getNotificationDate();
    }

    @Override
    public IssueRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public IssueRecord value2(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public IssueRecord value3(String value) {
        setUrl(value);
        return this;
    }

    @Override
    public IssueRecord value4(LocalDateTime value) {
        setNotificationDate(value);
        return this;
    }

    @Override
    public IssueRecord values(Integer value1, String value2, String value3, LocalDateTime value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached IssueRecord
     */
    public IssueRecord() {
        super(Issue.ISSUE);
    }

    /**
     * Create a detached, initialised IssueRecord
     */
    public IssueRecord(Integer id, String title, String url, LocalDateTime notificationDate) {
        super(Issue.ISSUE);

        setId(id);
        setTitle(title);
        setUrl(url);
        setNotificationDate(notificationDate);
    }
}
