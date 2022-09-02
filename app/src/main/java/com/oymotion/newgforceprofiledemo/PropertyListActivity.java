package com.oymotion.newgforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 05/12/2021
 * Time: 03:31
 * Description:
 */
class OpenQuestionListActivity extends AppCompatActivity{
//    private static final String TAG = "OpenQuestionManageActivity";
//
//    @BindView(R.id.btn_add_question)
//    Button btn_add_question;
//
//    private RecyclerView rv_questionList;
//    private OpenQuestionListAdapter questionListAdapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    private AddOpenQuestionDialog addOpenQuestionDialog;
//    private PopupDialog popupDialog;
//
//    private GForceDatabaseOpenHelper dbHelper;
//    private SQLiteDatabase db;
//
//    int prj_id = -1;
//    int id;
//    int id_clicked;
//    ArrayList<Question> questionList;
//
//    private OpenQuestionListAdapter.OnItemListener onItemListener = new OpenQuestionListAdapter.OnItemListener() {
//        @Override
//        public void OnItemClickListener(View view, int position) {
//            int childAdapterPosition = rv_questionList.getChildAdapterPosition(view);
//            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
////            Intent intent = new Intent(OpenQuestionManageActivity.this, ExperimentSettingMenuActivity.class);
//////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
////            p_id_clicked = questionList.get(childAdapterPosition).getPid();
////            intent.putExtra("p_id", p_id_clicked);
////            startActivity(intent);
//        }
//
//        @Override
//        public void OnItemLongClickListener(View view, int position) {
//            int childAdapterPosition = rv_questionList.getChildAdapterPosition(view);
//            Log.i(TAG, "OnItemLongClickListener: "+childAdapterPosition+"question_size:"+questionList.size());
//
//            id_clicked = questionList.get(childAdapterPosition).getId();
//            Log.i(TAG, "OnItemLongClickListener: " +"pid: "+ id_clicked);
//            FragmentManager fm = getSupportFragmentManager();
//            popupDialog = new PopupDialog();
//            popupDialog.show(fm,"fragment_edit_delete_question");
//
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_properties);
//        ButterKnife.bind(this);
//        setTitle("Manage Properties");
//
//        try {
//            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
//            db = dbHelper.getWritableDatabase();
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//        }
//
//        Intent intent = this.getIntent();
//        prj_id = intent.getIntExtra("prj_id", -1);
//        id = intent.getIntExtra("id", -1);
//
//        Log.i(TAG, "onCreate: "+"prj_id:"+prj_id+"id"+id);
//        btn_add_question = findViewById(R.id.btn_add_question);
//        initData();
//        initView();
//    }
//
//    private void initData() {
//        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        questionList = getData();
//        questionListAdapter = new OpenQuestionListAdapter(questionList);
//        questionListAdapter.setOnItemListener(onItemListener);
//    }
//
//    private void initView() {
//        rv_questionList = (RecyclerView) findViewById(R.id.rv_project_list);
//        // 设置布局管理器
//        rv_questionList.setLayoutManager(layoutManager);
//        // 设置adapter
//        rv_questionList.setAdapter(questionListAdapter);
//    }
//
//    @NotNull
//    private ArrayList<Question> getData() {
//        ArrayList<Question> data = new ArrayList<>();
//        data = Question.getQuestionList(db,prj_id);
//        return data;
//    }
//
//    @OnClick(R.id..)
//    public void onAddClick() {
//        FragmentManager fm = getSupportFragmentManager();
//        addOpenQuestionDialog = new AddOpenQuestionDialog(prj_id,-1);
//        addOpenQuestionDialog.show(fm,"fragment_add_question");
//
//    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onDialogPositiveClick(DialogFragment dialog) {
//        Log.i(TAG, "onDialogPositiveClick: ");
//        questionListAdapter.updateData(getData());
//    }
//    @Override
//    public void onDialogNegativeClick(DialogFragment dialog) {
//        dialog.getDialog().cancel();
//
//    }

}
