package org.hisp.dhis.android.datacapture.ui.fragments.aggregate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;

import org.hisp.dhis.android.datacapture.ui.adapters.AutoCompleteDialogAdapter.OptionAdapterValue;
import org.hisp.dhis.android.datacapture.ui.fragments.AutoCompleteDialogFragment;
import org.hisp.dhis.android.sdk.core.persistence.loaders.Query;

import java.util.List;

public class CategoryDialogFragment extends AutoCompleteDialogFragment
        implements LoaderCallbacks<List<OptionAdapterValue>> {
    public static final int ID = 456451;
    private static final int LOADER_ID = 4234421;
    private static final String CATEGORY_ID = "args:category";

    public static CategoryDialogFragment newInstance(OnOptionSelectedListener listener,
                                                     String categoryId) {
        CategoryDialogFragment fragment = new CategoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        fragment.setOnOptionSetListener(listener);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDialogId(ID);
    }

    @Override
    public Loader<List<OptionAdapterValue>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID && isAdded()) {
            /* List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            modelsToTrack.add(CategoryOption.class);
            String categoryId = getArguments().getString(CATEGORY_ID);
            return new DbLoader<>(getActivity(), modelsToTrack,
                    new CategoryOptionQuery(categoryId)); */
            return null;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<OptionAdapterValue>> loader,
                               List<OptionAdapterValue> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            getAdapter().swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<OptionAdapterValue>> loader) {
        if (loader != null && loader.getId() == LOADER_ID) {
            getAdapter().swapData(null);
        }
    }

    static class CategoryOptionQuery implements Query<List<OptionAdapterValue>> {
        private final String mCategoryId;

        public CategoryOptionQuery(String categoryId) {
            mCategoryId = categoryId;
        }

        @Override
        public List<OptionAdapterValue> query(Context context) {
            /* List<CategoryOption> options = Category.getRelatedOptions(mCategoryId);
            List<OptionAdapterValue> values = new ArrayList<>();
            if (options != null && !options.isEmpty()) {
                Collections.sort(options, CategoryOption.DISPLAY_NAME_MODEL_COMPARATOR);
                for (CategoryOption option : options) {
                    values.add(new OptionAdapterValue(option.getId(), option.getDisplayName(), mCategoryId));
                }
            }
            return values; */
            return null;
        }
    }
}
