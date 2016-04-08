package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;
import java.util.List;

/**
 * Base abstract class for an RecyclerView.Adapter
 * <p/>
 * <p>Adapters provide a basic logic for store, add, remove and get any item from a collection
 * additionally it contain BaseViewHolder to handle click to the list item
 * and highlight selected item.</p>
 */

public abstract class SimpleBaseAdapter<T, VH extends SimpleBaseAdapter.BaseViewHolder>
    extends RecyclerView.Adapter<VH> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    protected List<T> objects;

    private OnItemClickListener onItemClickListener;
    protected Context context;
    private View selectedView;

    private boolean selectableView = true;

    public SimpleBaseAdapter(final List<T> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(final T object) {
        objects.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        final int size = getItemCount();
        objects.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(final Collection<T> collection) {
        objects.addAll(collection);
        notifyItemRangeInserted(getItemCount() - 1, collection.size());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public T getItem(final int position) {
        return objects.get(position);
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
        return objects.indexOf(item);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(final T object, int index) {
        objects.add(index, object);
        notifyItemInserted(index);
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        final int position = getPosition(object);
        objects.remove(position);
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
            if (selectableView) {
                view.setSelected(true);
                notifyItemChanged(getAdapterPosition());
            }
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected void setSelectable(boolean selectableView) {
        this.selectableView = selectableView;
    }
}
