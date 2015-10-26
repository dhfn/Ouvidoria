package br.ufrr.promobile.ufrrmobile.ouvidoria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String SHARED_PREFERENCES = "user";
    private static final String SP_IS_REGISTERED = "isRegistered";
    private static final String SP_NAME = "name";
    private static final String SP_EMAIL = "email";
    private static final int MESSAGE_TYPE_SENDER = 1;
    private static final int MESSAGE_TYPE_RECEIVER = 2;
    private static final String LIST_KEY = "mensagens";

    private RecyclerView rvChat;
    private List<Message> messages;
    private EditText etMessage;
    private Button btSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Logger.d("onCreate");

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        if (!preferences.getBoolean(SP_IS_REGISTERED, false)){
            Logger.d("Usuário não registrado, abrindo IdentificationActivity");
            Intent intent = new Intent(this, IdentificationActivity.class);
            startActivity(intent);
            finish();
        }else {
            Logger.d("Usuário já foi registrado, Continuando");

            if(savedInstanceState != null){
                messages = savedInstanceState.getParcelableArrayList(LIST_KEY);
            }else{
                messages = new ArrayList<>();
            }

            rvChat = (RecyclerView) findViewById(R.id.rv_chat);
            etMessage = (EditText) findViewById(R.id.et_msg);
            btSendMessage = (Button) findViewById(R.id.bt_send);
            btSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!etMessage.getText().toString().trim().isEmpty()) {
                        Message message = new Message();
                        message.setText(etMessage.getText().toString());
                        message.setRegDate(new Date().getTime());
                        message.setType(MESSAGE_TYPE_SENDER);

                        etMessage.setText("");

                        ((MessageAdapter) rvChat.getAdapter())
                                .addListItem(message, 0);
                        rvChat.smoothScrollToPosition(0);

                        /*message = new Message();
                        message.setText("Mensagem de resposta");
                        message.setRegDate(new Date().getTime());
                        message.setType(MESSAGE_TYPE_RECEIVER);

                        ((MessageAdapter) rvChat.getAdapter())
                                .addListItem(message, 0);
                        rvChat.smoothScrollToPosition(0);*/

                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Logger.d("onStart");

        rvChat.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        rvChat.setLayoutManager(llm);

        rvChat.setAdapter(new MessageAdapter(this, messages));
        runClock();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIST_KEY, (ArrayList<Message>) messages);
        super.onSaveInstanceState(outState);
    }

    /**
     * Não funciona ainda!
     * */
    private void runClock(){
        Logger.d("runClock");
        new Thread(){
            @Override
            public void run() {
                Logger.d("Entrou na thread de atualização do horário");
                SystemClock.sleep(600);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rvChat != null){
                            rvChat.getAdapter().notifyDataSetChanged();
                            runClock();
                            Logger.d("Atualizar relogio.");
                        }else {
                            Logger.d("rvChat is null");
                        }
                    }
                });
            }
        };
    }









    /**
     * Adapter do cicleView - vai ser bem grande
     *
     * */

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
        private Context mContext;
        private List<Message> messList;
        private LayoutInflater mLayoutInflater;

        public MessageAdapter(Context context, List<Message> messages){
            mContext = context;
            messList = messages;
            mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_list_msg, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Message message = messList.get(position);

            if (message.getType() == MESSAGE_TYPE_SENDER){
                holder.llSender.setVisibility(View.VISIBLE);
                holder.llReceiver.setVisibility(View.GONE);

                holder.tvMessageSender.setText(message.getText());
                holder.tvHoraSender.setText(Util.getTimeAgo(message.getRegDate()));

                /*holder.llSender.setVisibility(View.GONE);
                holder.llReceiver.setVisibility(View.VISIBLE);

                holder.tvMessageReceiver.setText(message.getText());
                holder.tvHoraReceiver.setText(Util.getTimeAgo(message.getRegDate()));*/

            }else{
                holder.llSender.setVisibility(View.GONE);
                holder.llReceiver.setVisibility(View.VISIBLE);

                holder.tvMessageReceiver.setText(message.getText());
                holder.tvHoraReceiver.setText(Util.getTimeAgo(message.getRegDate()));

                /*holder.llSender.setVisibility(View.VISIBLE);
                holder.llReceiver.setVisibility(View.GONE);

                holder.tvMessageSender.setText(message.getText());
                holder.tvHoraSender.setText(Util.getTimeAgo(message.getRegDate()));*/
            }
        }

        @Override
        public int getItemCount() {
            return messList.size();
        }


        public void addListItem(Message m, int position){
            messList.add(position, m);
            notifyItemInserted(position);
        }


        public void removeListItem(int position){
            messList.remove(position);
            notifyItemRemoved(position);
        }

        /**
         * ViewHolder
         *
         * pode ser implementado a interface de click, devendo ser feita a implementação do método
         * e a aplicação dele no contrutor dessa classe no objeto itemView;
         * */
        public class MyViewHolder extends RecyclerView.ViewHolder{
            public LinearLayout llSender;
            public TextView tvMessageSender;
            public TextView tvHoraSender;
            public LinearLayout llReceiver;
            public TextView tvMessageReceiver;
            public TextView tvHoraReceiver;

            public MyViewHolder(View itemView) {
                super(itemView);

                llSender = (LinearLayout) itemView.findViewById(R.id.ll_sender);
                llReceiver = (LinearLayout) itemView.findViewById(R.id.ll_receiver);
                tvMessageSender = (TextView) itemView.findViewById(R.id.tv_msg_sender);
                tvMessageReceiver = (TextView) itemView.findViewById(R.id.tv_msg_receiver);
                tvHoraSender = (TextView) itemView.findViewById(R.id.tv_date_sender);
                tvHoraReceiver = (TextView) itemView.findViewById(R.id.tv_date_receiver);

                //Aqui deve ser aplicado ao objeto itemView o metodo de onClick ou onLongClick
            }


        }
    }
}
