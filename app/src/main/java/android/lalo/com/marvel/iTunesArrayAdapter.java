package android.lalo.com.marvel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lalo on 1/30/18.
 */

public class iTunesArrayAdapter extends ArrayAdapter<iTunes>{
    //Give a collection from type iTunes
    private ArrayList<iTunes> arrayList;

    public iTunesArrayAdapter(Context context, int resource, List<iTunes> objects) {
        super(context, resource, objects);
        arrayList = (ArrayList<iTunes>)objects;
    }

    //Rescatar datos en el json

    //Regresa un renglon a la vez con los datos que tenemos
    //n datos = n rengloes
    //Position es donde estamos, convertView es para crear el renglon
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Primer forma, mandar al constructor la collection
        iTunes iTunes = arrayList.get(position);

        if (convertView == null){
            //LayoutInflater reconstruye una vista
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itunes_layout,parent, false);
        }

        TextView collectioName = (TextView)convertView.findViewById(R.id.CollectionName);
        TextView trackName = (TextView)convertView.findViewById(R.id.TrackName);
        TextView trackPrice = (TextView)convertView.findViewById(R.id.TrackPrice);
        collectioName.setText(iTunes.collectionName);
        trackName.setText(iTunes.trackName);
        trackPrice.setText(iTunes.trackPrice+"");

        //Return ese renglon
        return convertView;
    }
}
