package io.mattcarroll.hover;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 */
public abstract class HoverMenu {

    private static final String TAG = "HoverMenu";

    private List<Section> mSections = new ArrayList<>();
    private ListUpdateCallback mListUpdateCallback;

    public abstract int getSectionCount();

    @Nullable
    public abstract Section getSection(int index);

    @Nullable
    public abstract Section getSection(@NonNull SectionId sectionId);

    @NonNull
    public abstract List<Section> getSections();

    void setUpdatedCallback(@Nullable ListUpdateCallback listUpdatedCallback) {
        mListUpdateCallback = listUpdatedCallback;
    }

    public void notifyMenuChanged() {
        List<Section> oldSections = mSections;
        List<Section> newSections = getSections();
        mSections = newSections;

        if (null != mListUpdateCallback) {
            DiffUtil.Callback diffCallback = new MenuDiffCallback(oldSections, newSections);
            // calculateDiff() can be long-running.  We let it run synchronously because we don't
            // expect many Sections.
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback, true);
            result.dispatchUpdatesTo(mListUpdateCallback);
        }
    }

    public static class SectionId {

        private String mId;

        public SectionId(@NonNull String id) {
            mId = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SectionId sectionId = (SectionId) o;

            return mId.equals(sectionId.mId);

        }

        @Override
        public int hashCode() {
            return mId.hashCode();
        }

        @Override
        public String toString() {
            return mId;
        }
    }

    public static class Section {

        private final SectionId mId;
        private final View mTabView;
        private final Content mContent;

        public Section(@NonNull SectionId id, @NonNull View tabView, @NonNull Content content) {
            mId = id;
            mTabView = tabView;
            mContent = content;
        }

        @NonNull
        public SectionId getId() {
            return mId;
        }

        @NonNull
        public View getTabView() {
            return mTabView;
        }

        @NonNull
        public Content getContent() {
            return mContent;
        }
    }

    private static class MenuDiffCallback extends DiffUtil.Callback {

        private final List<Section> mOldList;
        private final List<Section> mNewList;

        private MenuDiffCallback(@NonNull List<Section> oldList, @NonNull List<Section> newList) {
            mOldList = oldList;
            mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).getId().equals(mNewList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Section oldSection = mOldList.get(oldItemPosition);
            Section newSection = mNewList.get(newItemPosition);

            return oldSection.mTabView.equals(newSection.getTabView())
                    && oldSection.getContent().equals(newSection.getContent());
        }
    }
}
