package com.accipio.tutorme;

/**
 * Created by rachel on 2016-11-03.
 */

import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TutorsAdapter extends RecyclerView.Adapter<TutorsAdapter.TutorViewHolder> implements Filterable {

    protected ArrayList<Tutor> mDataSet;
    protected ArrayList<Tutor> filteredList;
    protected ArrayList<Tutor> original;
    protected boolean isAdmin;

    public TutorsAdapter(ArrayList<Tutor> mDataSet, boolean admin) {
        this.mDataSet = mDataSet;
        this.original = mDataSet;
        this.filteredList = mDataSet;
        this.isAdmin = admin;
    }

    @Override
    public TutorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(isAdmin){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_admin, parent, false);
            TutorViewHolder tutorViewHolder = new TutorViewHolder(v);
            return tutorViewHolder;

        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
            TutorViewHolder tutorViewHolder = new TutorViewHolder(v);
            return tutorViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(TutorViewHolder holder, int position) {
        holder.name.setText(filteredList.get(position).getName());
        holder.desc.setText(filteredList.get(position).getDesc());
        holder.rate.setText("$" + filteredList.get(position).getRate());

        float rating = filteredList.get(position).getRating();
        holder.rating.setRating(rating);
        //holder.rating.setStepSize(0.01f);
    }

    @Override
    public int getItemCount() {
        if (filteredList != null) {
            return filteredList.size();
        }
        return 0;
    }

    public class BandaidSOln implements View.OnClickListener{

        public int id;
        public String tutorId;
        public BandaidSOln(int idAYY){
            //tutorId = filteredList.get(idAYY).getId();
            id = idAYY;
            JSONParser jsonParser = new JSONParser();
            List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
            para.add(new Pair("tutor_id", id));
            jsonParser.request("http://ec2-54-245-142-221.us-west-2.compute.amazonaws.com/delete_Tutor.php", para, "POST", "delete_Tutor");
            System.out.println(id);

        }


        @Override
        public void onClick(View view) {
            System.out.println("pressed -------------------------------");
        }
    }
    public class TutorViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView icon, name, desc, rate;
        RatingBar rating;
        Button delete;


        TutorViewHolder(View itemView) {
            super(itemView);

            if(isAdmin){
                delete = (Button)  itemView.findViewById(R.id.deleteButton);
                delete.setOnClickListener(new BandaidSOln(new Integer(filteredList.get(0).getId())));
                cardView = (CardView) itemView.findViewById(R.id.browse_layout);
                icon = (TextView) itemView.findViewById(R.id.icon2);
                name = (TextView) itemView.findViewById(R.id.name2);
                desc = (TextView) itemView.findViewById(R.id.desc2);
                rate = (TextView) itemView.findViewById(R.id.rate2);
                rating = (RatingBar) itemView.findViewById(R.id.rating2);
            }
            else {


                cardView = (CardView) itemView.findViewById(R.id.browse_layout);
                icon = (TextView) itemView.findViewById(R.id.icon);
                name = (TextView) itemView.findViewById(R.id.name);
                desc = (TextView) itemView.findViewById(R.id.desc);
                rate = (TextView) itemView.findViewById(R.id.rate);
                rating = (RatingBar) itemView.findViewById(R.id.rating);
            }
        }


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                filteredList = (ArrayList<Tutor>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Tutor> FilteredArrList = new ArrayList<Tutor>();

                if (original == null) {
                    original = new ArrayList<Tutor>(mDataSet);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = original.size();
                    results.values = original;
                }
                else {
                    boolean none = false;
                    String constraints[] = constraint.toString().toLowerCase().split("-");
                    String rateStr = constraints[0];
                    String ratingStr = constraints[1];
                    String courseStr = constraints[2];
                    if (courseStr.equals("none")) {
                        none = true;
                        courseStr = " ";
                    }

                    int status = Integer.parseInt(constraints[3]);
                    int rateNum = Integer.parseInt(rateStr.split("_")[1]);
                    int ratingNum = Integer.parseInt(ratingStr.split("_")[1]);

                    for (Tutor item : mDataSet) {
                        if ((Integer.parseInt(item.getRate()) <= rateNum) && (item.getRating() >= ratingNum) && (item.getStatus() >= status)) {
                            if (!none) {
                                for (int i = 0; i < item.getCourses().size(); i++) {
                                    if (item.getCourses().get(i).toLowerCase().equals(courseStr)) {
                                        FilteredArrList.add(item);
                                    }
                                }
                            }
                            if (none) {
                                FilteredArrList.add(item);
                            }
                        }
                    }
                    results.values = FilteredArrList;
                    results.count = FilteredArrList.size();
                }
                return results;
            }
        };
        return filter;
    }
}

