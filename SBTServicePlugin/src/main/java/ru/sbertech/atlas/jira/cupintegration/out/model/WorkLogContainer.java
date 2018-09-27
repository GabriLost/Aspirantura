package ru.sbertech.atlas.jira.cupintegration.out.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Yaroslav Astafiev on 26/11/2015.
 * Department of analytical solutions and system services improovement.
 */
public class WorkLogContainer {
    final float timeSpent;
    final Date startDate;
    final DecimalFormat timeSpentFormatter;
    final SimpleDateFormat dateFormatter;

    public WorkLogContainer(Date startDate, long timeSpent) {
        this.startDate = startDate;
        this.timeSpent = ((float) timeSpent) / 3600;

        //actual effort formatter
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator(',');
        this.timeSpentFormatter = new DecimalFormat("0.00", otherSymbols);
        //date formatter
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    public float getTimeSpent() {
        return timeSpent;
    }

    public String getTimeSpentFormatted() {
        return timeSpentFormatter.format(timeSpent);
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateFormatted() {
        return dateFormatter.format(startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WorkLogContainer that = (WorkLogContainer) o;

        if (Float.compare(that.timeSpent, timeSpent) != 0)
            return false;
        return !(startDate != null ? !startDate.equals(that.startDate) : that.startDate != null);

    }

    @Override
    public int hashCode() {
        return Objects.hash(timeSpent, startDate);
    }
}
