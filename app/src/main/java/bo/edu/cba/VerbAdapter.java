package bo.edu.cba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class VerbAdapter extends ArrayAdapter<Verb> {

    public VerbAdapter(@NonNull Context context, @NonNull List<Verb> verbs) {
        super(context, 0, verbs); // Usamos 0 para el resource ID
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder;

        // 1. Inflar la vista si es necesario (o reutilizarla si convertView no es nulo)
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.textBase = listItemView.findViewById(R.id.textBase);
            holder.textPastTense = listItemView.findViewById(R.id.textPastTense);
            holder.textPastParticiple = listItemView.findViewById(R.id.textPastParticiple);
            listItemView.setTag(holder); // Almacenar el ViewHolder en la etiqueta de la vista
        } else {
            holder = (ViewHolder) listItemView.getTag(); // Reutilizar el ViewHolder existente
        }

        // 2. Obtener el objeto Verb para la posición actual
        Verb currentVerb = getItem(position);

        // 3. Poblar los TextViews con los datos del verbo
        if (currentVerb != null) {
            if (holder.textBase != null) {
                holder.textBase.setText(currentVerb.getBaseForm());
            }
            if (holder.textPastTense != null) {
                holder.textPastTense.setText(currentVerb.getPastTense());
            }
            if (holder.textPastParticiple != null) {
                holder.textPastParticiple.setText(currentVerb.getPastParticipie());
            }
        }

        return listItemView; // Devolver la vista completa para mostrarla
    }

    // ViewHolder pattern para mejorar el rendimiento
    private static class ViewHolder {
        TextView textBase;
        TextView textPastTense;
        TextView textPastParticiple;
    }
}
