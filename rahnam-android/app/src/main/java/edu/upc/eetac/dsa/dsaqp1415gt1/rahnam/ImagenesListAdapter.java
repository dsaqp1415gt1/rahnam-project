package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.Photo;

/**
 * Created by roco on 9/06/15.
 */
public class ImagenesListAdapter extends BaseAdapter{

    ArrayList<Photo> data;
    LayoutInflater inflater;

    private static class ViewHolder
    {
        //ImageView IMphoto;
        TextView TVtitulo;
        TextView TVcreador;
        TextView TVdate;
    }


    public ImagenesListAdapter(Context context, ArrayList<Photo> data)
    {
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return ((Photo) getItem(position)).getPhotoid();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_row_imagenes, null);
            viewHolder = new ViewHolder();
            //viewHolder.IMphoto = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.TVtitulo = (TextView) convertView.findViewById(R.id.TVtitulo);
            viewHolder.TVcreador = (TextView) convertView.findViewById(R.id.TVcreador);
            viewHolder.TVdate = (TextView) convertView.findViewById(R.id.TVdate);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //ImageView photo = ;
        String titulo = data.get(position).getTitle();
        String creador = data.get(position).getUsername();
        String date = SimpleDateFormat.getInstance().format(data.get(position).getLast_modified());
        viewHolder.TVtitulo.setText(titulo);
        viewHolder.TVcreador.setText(creador);
        viewHolder.TVdate.setText(date);
        return convertView;
    }





}
