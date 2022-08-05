package com.xendxby.dailyledger;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xendxby.dailyledger.database.LedgerDBHelper;
import com.xendxby.dailyledger.entity.LedgerItemInfo;
import com.xendxby.dailyledger.entity.PriceBoardController;
import com.xendxby.dailyledger.util.SharedUtil;
import com.xendxby.dailyledger.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private Button btn_four;
    private Button btn_five;
    private Button btn_six;
    private Button btn_seven;
    private Button btn_eight;
    private Button btn_nine;
    private Button btn_dot;
    private Button btn_zero;
    private Button btn_backspace;
    private Button btn_enter;
    private PriceBoardController priceBoardController;
    private Spinner sp_category;
    private SimpleAdapter outcomeAdapter;
    private SimpleAdapter incomeAdapter;

    private OutcomeCategoryItemSelectedListener outcomeCategoryItemSelectedListener;
    private IncomeCategoryItemSelectedListener incomeCategoryItemSelectedListener;

    private boolean isKeyboardHidden;                 // 表示数字键盘的状态，true表示键盘被隐藏，false表示键盘正在显示
    private LedgerItemInfo.RecordTypeEnum recordType;                // 表示当前记录的类型

    private ImageButton btn_keyboard_ctl;
    private GridLayout layout_keyboard;
    private TextView tv_price_board;

    private Button btn_outcome;
    private Button btn_income;

    private int categoryOutcomeIndex;
    private int categoryIncomeIndex;

    private static final int PRICE_MAX_INT_LENGTH = 7;

    private LedgerDBHelper ledgerDBHelper;

    public static final int[] categoryOutcomeIcons = {
            R.drawable.ic_food, R.drawable.ic_dining, R.drawable.ic_traffic, R.drawable.ic_dating,
            R.drawable.ic_daily, R.drawable.ic_financing, R.drawable.ic_entertainment, R.drawable.ic_studying,
            R.drawable.ic_other
    };

    public static final String[] categoryOutcomeNames = {
            "餐饮", "住宿", "交通", "恋爱", "日常", "理财", "娱乐", "学习", "其他"
    };

    public static final int[] categoryIncomeIcons = {
            R.drawable.ic_regular, R.drawable.ic_financing_income, R.drawable.ic_other
    };

    public static final String[] categoryIncomeNames = {
            "工资", "理财", "其他"
    };

    private static SharedUtil sharedUtilInstance;
    private static final String categoryOutcomeIndexTagName = "outcomeIndex";
    private static final String categoryIncomeIndexTagName = "incomeIndex";
    private EditText et_remark;
    private Button btn_to_ledger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isKeyboardHidden = false;

        priceBoardController = new PriceBoardController();
        sharedUtilInstance = SharedUtil.getInstance(this);

        ledgerDBHelper = LedgerDBHelper.getInstance(this);
        ledgerDBHelper.openReadLink();
        ledgerDBHelper.openWriteLink();

        outcomeCategoryItemSelectedListener = new OutcomeCategoryItemSelectedListener();
        incomeCategoryItemSelectedListener = new IncomeCategoryItemSelectedListener();

        btn_keyboard_ctl = findViewById(R.id.btn_keyboard_ctl);
        layout_keyboard = findViewById(R.id.layout_keyboard);
        tv_price_board = findViewById(R.id.tv_price_board);
        btn_outcome = findViewById(R.id.btn_outcome);
        btn_income = findViewById(R.id.btn_income);

        btn_one = findViewById(R.id.btn_one);
        btn_two = findViewById(R.id.btn_two);
        btn_three = findViewById(R.id.btn_three);
        btn_four = findViewById(R.id.btn_four);
        btn_five = findViewById(R.id.btn_five);
        btn_six = findViewById(R.id.btn_six);
        btn_seven = findViewById(R.id.btn_seven);
        btn_eight = findViewById(R.id.btn_eight);
        btn_nine = findViewById(R.id.btn_nine);
        btn_dot = findViewById(R.id.btn_dot);
        btn_zero = findViewById(R.id.btn_zero);
        btn_backspace = findViewById(R.id.btn_backspace);
        btn_enter = findViewById(R.id.btn_enter);
        et_remark = findViewById(R.id.et_remark);
        btn_to_ledger = findViewById(R.id.btn_to_ledger);

        btn_outcome.setOnClickListener(this);
        btn_income.setOnClickListener(this);

        btn_keyboard_ctl.setOnClickListener(this);
        tv_price_board.setOnClickListener(this);

        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_five.setOnClickListener(this);
        btn_six.setOnClickListener(this);
        btn_seven.setOnClickListener(this);
        btn_eight.setOnClickListener(this);
        btn_nine.setOnClickListener(this);
        btn_dot.setOnClickListener(this);
        btn_zero.setOnClickListener(this);
        btn_backspace.setOnClickListener(this);
        btn_enter.setOnClickListener(this);
        btn_to_ledger.setOnClickListener(this);

        List<Map<String, Object>> outcomeList = new ArrayList<>();
        for (int i = 0; i < categoryOutcomeIcons.length; i++) {
            Map<String, Object> map = new HashMap<>();

            map.put("icon", categoryOutcomeIcons[i]);
            map.put("name", categoryOutcomeNames[i]);
            outcomeList.add(map);
        }

        List<Map<String, Object>> incomeList = new ArrayList<>();
        for (int i = 0; i < categoryIncomeIcons.length; i++) {
            Map<String, Object> map = new HashMap<>();

            map.put("icon", categoryIncomeIcons[i]);
            map.put("name", categoryIncomeNames[i]);
            incomeList.add(map);
        }

        outcomeAdapter = new SimpleAdapter(this, outcomeList,
                R.layout.category_item,
                new String[]{"icon", "name"},
                new int[]{R.id.iv_category_item_icon, R.id.tv_category_item_name});

        incomeAdapter = new SimpleAdapter(this, incomeList,
                R.layout.category_item,
                new String[]{"icon", "name"},
                new int[]{R.id.iv_category_item_icon, R.id.tv_category_item_name});

        sp_category = findViewById(R.id.sp_category);

        categoryOutcomeIndex = sharedUtilInstance.readInt(categoryOutcomeIndexTagName, 0);
        categoryIncomeIndex = sharedUtilInstance.readInt(categoryIncomeIndexTagName, 0);

        handleChangeRecordTypeToOutcome();
        handleSetOutcomeAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ledgerDBHelper.closeLink();
    }

    private void handleSetOutcomeAdapter() {
        sp_category.setAdapter(outcomeAdapter);
        sp_category.setSelection(categoryOutcomeIndex);
        sp_category.setOnItemSelectedListener(outcomeCategoryItemSelectedListener);
    }

    private void handleSetIncomeAdapter() {
        sp_category.setAdapter(incomeAdapter);
        sp_category.setSelection(categoryIncomeIndex);
        sp_category.setOnItemSelectedListener(incomeCategoryItemSelectedListener);
    }

    private class OutcomeCategoryItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            categoryOutcomeIndex = i;
            sharedUtilInstance.writeInt(categoryOutcomeIndexTagName, i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class IncomeCategoryItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            categoryIncomeIndex = i;
            sharedUtilInstance.writeInt(categoryIncomeIndexTagName, i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void handleKeyboardHide() {
        layout_keyboard.setVisibility(View.GONE);
        btn_keyboard_ctl.setImageResource(R.drawable.ic_button_keyboard_show);
        isKeyboardHidden = true;
    }

    private void handleKeyboardShow() {
        layout_keyboard.setVisibility(View.VISIBLE);
        btn_keyboard_ctl.setImageResource(R.drawable.ic_button_keyboard_hide);
        isKeyboardHidden = false;
    }

    private void handleChangeRecordTypeToOutcome() {
        btn_outcome.setBackgroundResource(R.drawable.bg_tag_selected);
        btn_outcome.setTextColor(getResources().getColor(R.color.theme_color));
        btn_income.setBackgroundResource(R.color.white);
        btn_income.setTextColor(getResources().getColor(R.color.black));

        tv_price_board.setBackgroundResource(R.drawable.bg_underline_outcome);
        tv_price_board.setTextColor(getResources().getColor(R.color.red_outcome));

        recordType = LedgerItemInfo.RecordTypeEnum.OUTCOME;
    }

    private void handleChangeRecordTypeToIncome() {
        btn_income.setBackgroundResource(R.drawable.bg_tag_selected);
        btn_income.setTextColor(getResources().getColor(R.color.theme_color));
        btn_outcome.setBackgroundResource(R.color.white);
        btn_outcome.setTextColor(getResources().getColor(R.color.black));

        tv_price_board.setBackgroundResource(R.drawable.bg_underline_income);
        tv_price_board.setTextColor(getResources().getColor(R.color.green_income));

        recordType = LedgerItemInfo.RecordTypeEnum.INCOME;
    }

    private void handleKeyboardInputButton(char c) {
        if (c != '.' && !priceBoardController.isDecStart() && priceBoardController.getIntLength() >= PRICE_MAX_INT_LENGTH) {
            return;
        }

        priceBoardController.push(c);

        tv_price_board.setText(priceBoardController.toString());
    }

    private void handleAddLedgerItem() {
        if (priceBoardController.doubleValue() < 0.005) {
            Toast.makeText(this, "金额不能为0！", Toast.LENGTH_LONG).show();
            return;
        }

        ledgerDBHelper.openWriteLink();
        LedgerItemInfo info = new LedgerItemInfo(
                0,
                TimeUtil.getCurrentTime(),
                recordType,
                recordType == LedgerItemInfo.RecordTypeEnum.OUTCOME ? categoryOutcomeIndex : categoryIncomeIndex,
                et_remark.getText().toString().trim(),
                priceBoardController.toBidDecimal()
        );
        Log.d("xbylog", "handleAddLedgerItem: info:" + info.toString());
        ledgerDBHelper.insertLedgerItem(info);
        ledgerDBHelper.closeLink();
        Toast.makeText(this, "添加记录成功！", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_keyboard_ctl:
                if (isKeyboardHidden) {
                    handleKeyboardShow();
                } else {
                    handleKeyboardHide();
                }
                break;
            case R.id.tv_price_board:
                if (isKeyboardHidden) {
                    handleKeyboardShow();
                }
                break;
            case R.id.btn_outcome:
                if (recordType != LedgerItemInfo.RecordTypeEnum.OUTCOME) {
                    handleChangeRecordTypeToOutcome();
                    handleSetOutcomeAdapter();
                }
                break;
            case R.id.btn_income:
                if (recordType != LedgerItemInfo.RecordTypeEnum.INCOME) {
                    handleChangeRecordTypeToIncome();
                    handleSetIncomeAdapter();
                }
                break;
            case R.id.btn_one:
                handleKeyboardInputButton('1');
                break;
            case R.id.btn_two:
                handleKeyboardInputButton('2');
                break;
            case R.id.btn_three:
                handleKeyboardInputButton('3');
                break;
            case R.id.btn_four:
                handleKeyboardInputButton('4');
                break;
            case R.id.btn_five:
                handleKeyboardInputButton('5');
                break;
            case R.id.btn_six:
                handleKeyboardInputButton('6');
                break;
            case R.id.btn_seven:
                handleKeyboardInputButton('7');
                break;
            case R.id.btn_eight:
                handleKeyboardInputButton('8');
                break;
            case R.id.btn_nine:
                handleKeyboardInputButton('9');
                break;
            case R.id.btn_zero:
                handleKeyboardInputButton('0');
                break;
            case R.id.btn_dot:
                handleKeyboardInputButton('.');
                break;
            case R.id.btn_backspace:
                priceBoardController.pop();
                tv_price_board.setText(priceBoardController.toString());
                break;
            case R.id.btn_enter:
                handleAddLedgerItem();
                break;
            case R.id.btn_to_ledger:
                startActivity(new Intent(this, LedgerDetailActivity.class));
        }
    }
}