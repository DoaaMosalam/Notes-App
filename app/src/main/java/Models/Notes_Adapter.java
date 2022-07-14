package Models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.application.notesapplication.Details_Notes;
import com.application.notesapplication.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Notes_Adapter extends RecyclerView.Adapter<Notes_Adapter.viewHolder>{
    List<Notes> notesList = new ArrayList<>();
    private Context context;
    final int code = getRandomColor();
    //constructor.
    public Notes_Adapter(Context context, List<Notes> notesList) {
        this.notesList = notesList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notes_view_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.onBind(notesList.get(position));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void FilterList(List<Notes> filterList) {
        notesList = filterList;
        notifyDataSetChanged();
    }
    //inner class View Holder.
    public class viewHolder extends RecyclerView.ViewHolder{
        Notes notes;
        TextView _contentsNotes;
        TextView _timeDate;
        ImageView _imageNotes;
        View view;
        CardView mCardview;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            _contentsNotes=itemView.findViewById(R.id.contentsNotes);
            _timeDate=itemView.findViewById(R.id.timeDate);
            _imageNotes=itemView.findViewById(R.id.imageNotes);
            mCardview=itemView.findViewById(R.id.noteCard);
            view=itemView;
        }
        void onBind(final Notes n){
            this.notes=n;
            int position = getAdapterPosition();
            _contentsNotes.setText(n.getContents_Notes());
            _timeDate.setText(n.getTempleTime());
//            _imageNotes.setImageResource(n.getImagePath());
            //Glide Library,Image View.
            Glide.with(context)
                    .load(notesList.get(position).getImagePath())
                    .into(_imageNotes);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCardview.setCardBackgroundColor(view.getResources().getColor(getRandomColor(),null));
            }

            /*
             * this on appear message when click item
             * عند الضغط علي واحد من عناصر الريسيكال فيو تذهب الي كلاس Details.class لعرض محتويات الملاحظه */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Notes n = notesList.get(position);
                    Intent i = new Intent(view.getContext(), Details_Notes.class);
                    i.putExtra("content", String.valueOf(notesList.get(position).getContents_Notes()));
                    i.putExtra("id", String.valueOf(notesList.get(position).getId()));
                    i.putExtra("image",notesList.get(position).getImagePath());
                    i.putExtra("code",code);
                    view.getContext().startActivity(i);
                }
            });

        }
    }
    /*This method to random color list notes
     * used Random and collection some colors
     * هذا الميثود يعمل علي اختيار االوان الملاحظه في االقائمة بطريقه عشوائية
     * وتم تعويض هذا الكود في متغير mcardView.setCardBackgroundColor*/
    private int getRandomColor() {
        List<Integer> color_Notes = new ArrayList<>();
        color_Notes.add(R.color.blue);
        color_Notes.add(R.color.yellow);
        color_Notes.add(R.color.skyblue);
        color_Notes.add(R.color.lightPurple);
        color_Notes.add(R.color.lightGreen);
        color_Notes.add(R.color.gray);
        color_Notes.add(R.color.pink);
        color_Notes.add(R.color.red);
        color_Notes.add(R.color.greenlight);
        color_Notes.add(R.color.notgreen);
        color_Notes.add(R.color.teal_200);
        color_Notes.add(R.color.teal_700);
        color_Notes.add(R.color.purple_200);
        color_Notes.add(R.color.purple_500);
        color_Notes.add(R.color.purple_700);

        Random randomColor = new Random();
        int numbers = randomColor.nextInt(color_Notes.size());
        return color_Notes.get(numbers);
    }
}
