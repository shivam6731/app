package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;
import java.util.List;

public abstract class SimpleBaseAdapter<T, VH extends SimpleBaseAdapter.BaseViewHolder>
    extends RecyclerView.Adapter<VH> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    protected List<T> mObjects;

    private OnItemClickListener onItemClickListener;
    protected Context context;
    private View selectedView;

    public SimpleBaseAdapter(final List<T> objects, Context context) {
        mObjects = objects;
        this.context = context;
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(final T object) {
        mObjects.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        final int size = getItemCount();
        mObjects.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(final Collection<T> collection) {
        mObjects.addAll(collection);
        notifyItemRangeInserted(getItemCount() - 1, collection.size());
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public T getItem(final int position) {
        return mObjects.get(position);
    }

    public long getItemId(final int position) {
        return position;
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(final T item) {
        return mObjects.indexOf(item);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(final T object, int index) {
        mObjects.add(index, object);
        notifyItemInserted(index);

    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        final int position = getPosition(object);
        mObjects.remove(position);
        notifyItemRemoved(position);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            }
            if (selectedView != null) {
                selectedView.setSelected(false);
                selectedView = view;
            }
            view.setSelected(true);
            notifyItemChanged(getAdapterPosition());
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
