package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.Comment;

/**
 * Created by roco on 9/06/15.
 */
public class ComentariosListAdapter extends BaseAdapter {

    ArrayList<Comment> data;
    LayoutInflater inflater;

    private static class ViewHolder
    {
        TextView TVcreador;
        TextView TVcontent;
        TextView TVdate;
    }


    public ComentariosListAdapter(Context context, ArrayList<Comment> data)
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
        return ((Comment) getItem(position)).getCommentid();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_row_comentarios, null);
            viewHolder = new ViewHolder();
            viewHolder.TVcreador = (TextView) convertView.findViewById(R.id.TVcreador);
            viewHolder.TVcontent = (TextView) convertView.findViewById(R.id.TVcontent);
            viewHolder.TVdate = (TextView) convertView.findViewById(R.id.TVdate);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String content = data.get(position).getContent();
        String creador = data.get(position).getUsername();
        String date = SimpleDateFormat.getInstance().format(data.get(position).getLast_modified());
        viewHolder.TVcontent.setText(content);
        viewHolder.TVcreador.setText(creador);
        viewHolder.TVdate.setText(date);
        return convertView;

    }
}
