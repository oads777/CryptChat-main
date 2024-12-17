package com.worldsvoice.cryptchat.adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.worldsvoice.cryptchat.ChatActivity;
import com.worldsvoice.cryptchat.R;
import com.worldsvoice.cryptchat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.worldsvoice.cryptchat.utils.AndroidUtil;

public class SearchUserRyclerAdpater extends FirestoreRecyclerAdapter<UserModel, SearchUserRyclerAdpater.UserModelViewHolder> {

    private final Context context;

    // Ajuste no construtor para aceitar o Context
    public SearchUserRyclerAdpater(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        // Preenchendo os dados no ViewHolder
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent (context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use o context passado no construtor
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    // Classe ViewHolder para vincular os dados ao layout
    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilepic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilepic = itemView.findViewById(R.id.profile_pic_view);
        }
    }
}

