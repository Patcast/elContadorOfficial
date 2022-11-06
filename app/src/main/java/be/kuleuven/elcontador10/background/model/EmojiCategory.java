package be.kuleuven.elcontador10.background.model;

import android.util.Log;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.background.Caching;

public class EmojiCategory {
    private static final String TAG ="EmojiCategory" ;
    private String id;
    private String icon;
    private String title;
    private boolean isDeleted;
    private final List<String> type = new ArrayList<>();
    private boolean cashIn;

    public EmojiCategory(String icon, String title, boolean cashIn, List<String> type) {
        this.icon = icon;
        this.title = title;
        this.isDeleted = false;
        this.cashIn = cashIn;
        this.type.clear();
        this.type.addAll(type);
    }

    public EmojiCategory() {
    }

    public static void saveNewCategory(EmojiCategory newEmoji) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String urlNewTransactions = "/accounts/"+ Caching.INSTANCE.getChosenAccountId()+"/customCategories";
        db.collection(urlNewTransactions)
                .add(newEmoji)
                .addOnSuccessListener(documentReference -> newEmoji.setNewId(documentReference.getId()) )
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

    }

    private void setNewId(String id) {
        this.id = id;
        updateCategory(this);
    }

    public static void updateCategory(EmojiCategory newEmoji){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String urlNewTransactions = "/accounts/"+ Caching.INSTANCE.getChosenAccountId()+"/customCategories";
        db.collection(urlNewTransactions).document(newEmoji.getId())
                .set(newEmoji)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public static void deleteCategory(EmojiCategory emoji){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        emoji.setDeleted(true);

        String urlNewTransactions = "/accounts/"+ Caching.INSTANCE.getChosenAccountId()+"/customCategories";
        db.collection(urlNewTransactions).document(emoji.getId())
                .update("isDeleted", true)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public boolean requiresStakeHolderChosen(){
        return getType().contains(Caching.INSTANCE.TYPE_PAYABLES) || getType().contains(Caching.INSTANCE.TYPE_RECEIVABLES);
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public List<String> getType() {
        return type;
    }

    public boolean isCashIn() {
        return cashIn;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
