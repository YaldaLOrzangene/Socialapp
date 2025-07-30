package com.example.socialapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.List;

public class PostAdapter extends BaseAdapter {
	
	private Context context;
	private List<Post> postList;
	private DBHelper dbHelper;
	private String currentUsername;
	
	public PostAdapter(Context context, List<Post> postList) {
		this.context = context;
		this.postList = postList;
		this.dbHelper = new DBHelper(context);
		
		if (context instanceof MainActivity) {
			currentUsername = ((MainActivity) context).getCurrentUsername();
		}
	}
	
	@Override
	public int getCount() {
		return postList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return postList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	static class ViewHolder {
		TextView tvPostUser, tvPostTitle, tvPostContent;
		TextView tvLikeCount, tvCommentCount;
		ImageButton btnLike;
		ImageButton btnComment, btnDeletePost;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
			holder = new ViewHolder();
			holder.tvPostUser = convertView.findViewById(R.id.tvPostUser);
			holder.tvPostTitle = convertView.findViewById(R.id.tvPostTitle);
			holder.tvPostContent = convertView.findViewById(R.id.tvPostContent);
			holder.tvLikeCount = convertView.findViewById(R.id.tvLikeCount);
			holder.tvCommentCount = convertView.findViewById(R.id.tvCommentCount);
			holder.btnLike = convertView.findViewById(R.id.btnLike);
			holder.btnComment = convertView.findViewById(R.id.btnComment);
			holder.btnDeletePost = convertView.findViewById(R.id.btnDeletePost);
			convertView.setTag(holder);
			} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Post post = postList.get(position);
		int postId = Integer.parseInt(post.getId());
		int userId = dbHelper.getUserId(currentUsername);
		
		if (userId == -1) {
			Toast.makeText(context, "کاربر یافت نشد!", Toast.LENGTH_LONG).show();
			return convertView;
		}
		
		holder.tvPostUser.setText(post.getUsername());
		holder.tvPostTitle.setText(post.getTitle());
		holder.tvPostContent.setText(post.getContent());
		holder.tvLikeCount.setText(dbHelper.getLikeCount(postId) + " لایک");
		holder.tvCommentCount.setText(dbHelper.getCommentCount(postId) + " نظر");
		
		// وضعیت لایک
		if (dbHelper.hasUserLikedPost(userId, postId)) {
			holder.btnLike.setImageResource(R.drawable.like);
			} else {
			holder.btnLike.setImageResource(R.drawable.outlike);
		}
		
		// رویداد کلیک لایک
		holder.btnLike.setOnClickListener(v -> {
			if (dbHelper.hasUserLikedPost(userId, postId)) {
				boolean removed = dbHelper.removeLike(userId, postId);
				if (removed) {
					holder.btnLike.setImageResource(R.drawable.outlike);
					Toast.makeText(context, "لایک حذف شد", Toast.LENGTH_SHORT).show();
				}
				} else {
				boolean added = dbHelper.insertLike(userId, postId);
				if (added) {
					holder.btnLike.setImageResource(R.drawable.like);
					Toast.makeText(context, "پست لایک شد", Toast.LENGTH_SHORT).show();
				}
			}
			holder.tvLikeCount.setText(dbHelper.getLikeCount(postId) + " لایک");
		});
		
		// ✳️ رویداد کلیک روی تعداد لایک برای نمایش اسامی کاربران
		holder.tvLikeCount.setOnClickListener(v -> {
			List<String> likedUsers = dbHelper.getUsernamesWhoLikedPost(postId);
			
			if (likedUsers.isEmpty()) {
				Toast.makeText(context, "کسی این پست رو لایک نکرده", Toast.LENGTH_SHORT).show();
				return;
			}
			
			StringBuilder namesText = new StringBuilder();
			for (String name : likedUsers) {
				namesText.append("• ").append(name).append("\n");
			}
			
			new AlertDialog.Builder(context)
			.setTitle("لایک شده توسط")
			.setMessage(namesText.toString())
			.setPositiveButton("باشه", null)
			.show();
		});
		
		// رویداد کلیک کامنت
		holder.btnComment.setOnClickListener(v -> {
			Intent intent = new Intent(context, CommentActivity.class);
			intent.putExtra("post_id", postId);
			intent.putExtra("username", currentUsername);
			context.startActivity(intent);
		});
		
		// حذف پست برای نویسنده
		if (post.getUsername().equals(currentUsername)) {
			holder.btnDeletePost.setVisibility(View.VISIBLE);
			holder.btnDeletePost.setOnClickListener(v -> {
				boolean deleted = dbHelper.deletePost(postId);
				if (deleted) {
					Toast.makeText(context, "پست حذف شد", Toast.LENGTH_SHORT).show();
					postList.remove(position);
					notifyDataSetChanged();
					} else {
					Toast.makeText(context, "خطا در حذف پست", Toast.LENGTH_SHORT).show();
				}
			});
			} else {
			holder.btnDeletePost.setVisibility(View.GONE);
		}
		
		return convertView;
	}
}