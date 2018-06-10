package com.artyomd.rx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemViewHolder> {

	private List<Response> items;
	private Context context;

	public SearchAdapter(Context context) {
		this.context = context.getApplicationContext();
	}

	@NonNull
	@Override
	public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		return new ItemViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
		final Response item = items.get(position);
		holder.title.setText(item.getTitle());
		holder.title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
				context.startActivity(browserIntent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return items == null ? 0 : items.size();
	}

	public void setData(List<Response> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		private final TextView title;

		ItemViewHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(android.R.id.text1);
		}
	}
}
