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

package org.hisp.dhis.android.datacapture.ui.fragments.aggregate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.datacapture.R;
import org.hisp.dhis.android.datacapture.api.date.CustomDateIterator;
import org.hisp.dhis.android.datacapture.api.date.DateIteratorFactory;
import org.hisp.dhis.android.datacapture.api.models.DateHolder;
import org.hisp.dhis.android.datacapture.ui.adapters.SimpleAdapter;
import org.hisp.dhis.android.datacapture.ui.fragments.AutoCompleteDialogFragment.OnOptionSelectedListener;
import org.hisp.dhis.android.sdk.core.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.models.dataset.DataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class PeriodDialogFragment extends DialogFragment
        implements LoaderCallbacks<DataSet>,
        View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int ID = 7932678;
    private static final String TAG = PeriodDialogFragment.class.getName();
    private static final int LOADER_ID = 345234575;
    private static final String DATA_SET_ID = "args:dataSetId";

    @InjectView(R.id.simple_listview)
    ListView mListView;
    @InjectView(R.id.previous)
    Button mPrevious;
    @InjectView(R.id.next)
    Button mNext;
    @InjectView(R.id.dialog_label)
    TextView mDialogLabel;

    private SimpleAdapter<DateHolder> mAdapter;
    private OnOptionSelectedListener mListener;

    private CustomDateIterator<List<DateHolder>> mIterator;

    public static PeriodDialogFragment newInstance(OnOptionSelectedListener listener,
                                                   String dataSetId) {
        PeriodDialogFragment fragment = new PeriodDialogFragment();
        Bundle args = new Bundle();
        args.putString(DATA_SET_ID, dataSetId);
        fragment.setArguments(args);
        fragment.setOnItemClickListener(listener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_listview_period, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);

        mDialogLabel.setText(R.string.period);

        mAdapter = new SimpleAdapter<>(LayoutInflater.from(getActivity()));
        mAdapter.setStringExtractor(new ExtractPeriodLabel());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @OnClick({R.id.previous, R.id.next, R.id.close_dialog_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                mNext.setEnabled(true);
                mAdapter.swapData(mIterator.previous());
                break;
            }
            case R.id.next: {
                if (mIterator.hasNext()) {
                    List<DateHolder> dates = mIterator.next();
                    if (!mIterator.hasNext()) {
                        mNext.setEnabled(false);
                    }
                    mAdapter.swapData(dates);
                } else {
                    mNext.setEnabled(false);
                }
                break;
            }
            case R.id.close_dialog_button: {
                dismiss();
                break;
            }
        }
    }

    @OnItemClick(R.id.simple_listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            DateHolder date = mAdapter.getItemSafely(position);
            if (date != null) {
                mListener.onOptionSelected(ID, position, date.getDate(), date.getLabel(), null);
            }
            dismiss();
        }
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    public void setOnItemClickListener(OnOptionSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public Loader<DataSet> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID && bundle != null) {
            /* List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
            tablesToTrack.add(DataSet.class);
            String dataSetId = bundle.getString(DATA_SET_ID);
            return new DbLoader<>(getActivity().getApplication(),
                    tablesToTrack, new DataSetQuery(dataSetId)); */
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<DataSet> loader, DataSet dataSet) {
        if (loader != null && loader.getId() == LOADER_ID && dataSet != null) {
            mIterator = DateIteratorFactory.getDateIterator(
                    dataSet.getPeriodType(), dataSet.isAllowFuturePeriods()
            );

            mAdapter.swapData(mIterator.current());
            if (mIterator != null && mIterator.hasNext()) {
                mNext.setEnabled(true);
            } else {
                mNext.setEnabled(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<DataSet> dbRowLoader) {
    }


    static class DataSetQuery implements Query<DataSet> {
        private final String mDataSetId;

        public DataSetQuery(String dataSetId) {
            mDataSetId = dataSetId;
        }

        @Override
        public DataSet query(Context context) {
            /* return new Select().from(DataSet.class)
                    .where(Condition.column(DataSet$Table.ID).is(mDataSetId))
                    .querySingle(); */
            return null;
        }
    }

    static class ExtractPeriodLabel implements SimpleAdapter.ExtractStringCallback<DateHolder> {

        @Override
        public String getString(DateHolder object) {
            return object.getLabel();
        }
    }
}
