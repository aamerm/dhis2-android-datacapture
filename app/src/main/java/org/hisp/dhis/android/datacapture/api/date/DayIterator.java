/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.datacapture.api.date;

import org.hisp.dhis.android.datacapture.api.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String DATE_FORMAT = "YYYYMMdd";
    private static final String DATE_LABEL_FORMAT = "%s %s, %s";

    private boolean mAllowFP;
    private LocalDate mPeriod;
    private LocalDate mCheckDate;

    public DayIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        mCheckDate = new LocalDate(mPeriod);
    }

    @Override
    public boolean hasNext() {
        return hasNext(mCheckDate);
    }

    private boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            return currentDate.isAfter(date);
        }
    }

    @Override
    public List<DateHolder> current() {
        if (!hasNext()) {
            return previous();
        } else {
            return generatePeriod();
        }
    }

    @Override
    public List<DateHolder> next() {
        mPeriod = mPeriod.plusYears(1);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> previous() {
        mPeriod = mPeriod.minusYears(1);
        return generatePeriod();
    }

    @Override
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        mCheckDate = new LocalDate(mPeriod);

        int counter = 0;
        int quantity = mCheckDate.dayOfYear().getMaximumValue();

        while (hasNext(mCheckDate) && counter < quantity) {
            counter++;

            String date = mCheckDate.toString(DATE_FORMAT);

            String dName = mCheckDate.dayOfMonth().getAsString();
            String mName = mCheckDate.monthOfYear().getAsText();
            String yName = mCheckDate.year().getAsString();

            String label = String.format(DATE_LABEL_FORMAT, dName, mName, yName);

            mCheckDate = mCheckDate.plusDays(1);
            DateHolder holder = new DateHolder(date, label);
            dates.add(holder);
        }

        Collections.reverse(dates);
        return dates;
    }

}
