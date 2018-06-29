package com.example.a1.calculator;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.mozilla.javascript.Scriptable;

import java.util.Stack;


public class MainActivity extends AppCompatActivity {

    /******************************************************
     * Объявленные переменные
     ******************************************************/

    /***** Кнопка очищения *****/
    private Button btnClear;

    /***** Экраны ввода-вывода переменных *****/
    private TextView tvProcessor, tvResult;

    /***** Кнопки ввода чисел *****/
    private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, btnDegree, btnPostfix;

    /***** Кнопки математических операций *****/
    private Button btnMultiply, btnMinus, btnPlus, btnDivide, btnDecimal, btnBack, btnSmallBracket, btnEqual, btnPercentage;

    /***** Дополнительне переменные *****/
    private String processor;
    private Boolean isSmallBracketOpen;
    private HorizontalScrollView hsvUserInput;
    private int developedCounter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isSmallBracketOpen = false;
        developedCounter = 0;

        hsvUserInput = (HorizontalScrollView) findViewById(R.id.hsvInput);
        afterClick();

        btnClear = (Button) findViewById(R.id.btn_delete);
        tvProcessor = (TextView) findViewById(R.id.tv_process);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnOne = (Button) findViewById(R.id.btn_one);
        btnTwo = (Button) findViewById(R.id.btn_two);
        btnThree = (Button) findViewById(R.id.btn_three);
        btnFour = (Button) findViewById(R.id.btn_four);
        btnFive = (Button) findViewById(R.id.btn_five);
        btnSix = (Button) findViewById(R.id.btn_six);
        btnSeven = (Button) findViewById(R.id.btn_seven);
        btnEight = (Button) findViewById(R.id.btn_eight);
        btnNine = (Button) findViewById(R.id.btn_nine);
        btnZero = (Button) findViewById(R.id.btn_zero);


        btnMultiply = (Button) findViewById(R.id.btn_multiply);
        btnMinus = (Button) findViewById(R.id.btn_minus);
        btnPlus = (Button) findViewById(R.id.btn_plus);
        btnDivide = (Button) findViewById(R.id.btn_divide);

        btnDecimal = (Button)findViewById(R.id.btn_dot);
        btnBack = (Button)findViewById(R.id.btn_back);

        btnSmallBracket = (Button)findViewById(R.id.btn_small_bracket);
        btnEqual = (Button) findViewById(R.id.btn_equal);
        btnPercentage = (Button) findViewById(R.id.btn_percentage);

        btnDegree = (Button)findViewById(R.id.btn_degree);
        btnPostfix = (Button)findViewById(R.id.btn_postfix);

        tvProcessor.setText("0");
        tvResult.setText("");

        listen();
    }

    private double resulting(String input)
    {
        double result = 0;
        Stack<Double> temp = new Stack<>();

        for (int i = 0; i < input.length(); i++) //Для каждого символа в строке
        {
            //Если символ - цифра, то читаем все число и записываем на вершину стека
            if (Character.isDigit(input.charAt(i)))
            {
                String a = "";

                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i))) //Пока не разделитель
                {
                    a += input.charAt(i); //Добавляем
                    i++;
                    if (i == input.length()) break;
                }
                temp.push(Double.parseDouble(a)); //Записываем в стек
                i--;
            }
            else if (isOperator(input.charAt(i))) //Если символ - оператор
            {
                //Берем два последних значения из стека
                double a = temp.pop();
                double b = temp.pop();


                switch (input.charAt(i)) //И производим над ними действие, согласно оператору
                {
                    case '+': result = b + a; break;
                    case '-': result = b - a; break;
                    case '*': result = b * a; break;
                    case '/': result = b / a; break;

                    case '^': result = Math.pow(b, a);
                        break;
                }


                temp.push(result); //Результат вычисления записываем обратно в стек
            }
        }
        return temp.peek(); //Забираем результат всех вычислений из стека и возвращаем его
    }

    static private byte getPriority(char s)
    {
        switch (s)
        {
            case '(': return 0;
            case ')': return 1;
            case '+': return 2;
            case '-': return 3;
            case '*': return 4;
            case '/': return 4;
            case '^': return 5;
            default: return 6;
        }
    }

    static private boolean isOperator(char с)
    {
        if (("+-/*^()".indexOf(с) != -1))
            return true;
        return false;
    }

    static private boolean isDelimeter(char c)
    {
        if ((" =".indexOf(c) != -1))
            return true;
        return false;
    }

    public double compute(String input)
    {
        final String output = postExpress(input); //Преобразовываем выражение в постфиксную запись

        btnPostfix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResult.setText(output);
            }
        });

        double result = resulting(output); //Решаем полученное выражение
        return result; //Возвращаем результат
    }

    static private String postExpress(String input) {
        String output = ""; //Строка для хранения выражения
        Stack<Character> operStack = new Stack<>(); //Стек для хранения операторов

        for (int i = 0; i < input.length(); i++) //Для каждого символа в входной строке
        {
            //Разделители пропускаем
            if (isDelimeter(input.charAt(i)))
                continue; //Переходим к следующему символу

            //Если символ - цифра, то считываем все число
            if (Character.isDigit(input.charAt(i))) //Если цифра
            {
                //Читаем до разделителя или оператора, что бы получить число
                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i))) {
                    output += input.charAt(i); //Добавляем каждую цифру числа к нашей строке
                    i++; //Переходим к следующему символу

                    if (i == input.length()) break; //Если символ - последний, то выходим из цикла
                }

                output += " "; //Дописываем после числа пробел в строку с выражением
                i--; //Возвращаемся на один символ назад, к символу перед разделителем
            }

            //Если символ - оператор
            if (isOperator(input.charAt(i))) //Если оператор
            {
                if (input.charAt(i) == '(') //Если символ - открывающая скобка
                    operStack.push(input.charAt(i)); //Записываем её в стек
                else if (input.charAt(i) == ')') //Если символ - закрывающая скобка
                {
                    //Выписываем все операторы до открывающей скобки в строку
                    char s = operStack.pop();

                    while (s != '(') {
                        output += String.valueOf(s) + ' ';
                        s = operStack.pop();
                    }
                } else //Если любой другой оператор
                {
                    if (!operStack.isEmpty()) //Если в стеке есть элементы
                        if (getPriority(input.charAt(i)) <= getPriority(operStack.peek())) //И если приоритет нашего оператора меньше или равен приоритету оператора на вершине стека
                            output += String.valueOf(operStack.pop()) + " "; //То добавляем последний оператор из стека в строку с выражением

                    operStack.push(input.charAt(i)); //Если стек пуст, или же приоритет оператора выше - добавляем операторов на вершину стека

                }
            }
        }
        //Когда прошли по всем символам, выкидываем из стека все оставшиеся там операторы в строку
        while (!operStack.isEmpty())
            output += operStack.pop() + " ";

        return output; //Возвращаем выражение в постфиксной записи
    }


    /******************************************************
     * Кнопки on-Click
     ******************************************************/
    public void  listen(){

        /***** Ввода чисел *****/
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "1");
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "2");
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "3");
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "4");
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "5");
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "6");
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "7");
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "8");
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "9");
            }
        });

        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "0");
            }
        });

        /***** Математические и функциональные кнопки *****/
        btnMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "*");
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "-");
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "+");
            }
        });
        btnDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "/");
            }
        });
        btnDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + ".");
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                if(processor.length() > 0){
                    processor = processor.substring(0, processor.length()-1);
                    tvProcessor.setText(processor);
                }else{
                    tvResult.setText("0");
                }
            }
        });
        btnSmallBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                if(isSmallBracketOpen){
                    processor = tvProcessor.getText().toString();
                    tvProcessor.setText(processor + ")");
                    isSmallBracketOpen = false;
                }else{
                    processor = tvProcessor.getText().toString();
                    tvProcessor.setText(processor + "(");
                    isSmallBracketOpen = true;
                }
            }
        });

        btnPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "/100");

            }
        });


        btnSmallBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                if(isSmallBracketOpen){
                    processor = tvProcessor.getText().toString();
                    tvProcessor.setText(processor + ")");
                    isSmallBracketOpen = false;
                }else{
                    processor = tvProcessor.getText().toString();
                    tvProcessor.setText(processor + "(");
                    isSmallBracketOpen = true;
                }
            }
        });

        btnDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                try{
                if(isOperator(tvProcessor.getText().toString().charAt(tvProcessor.getText().toString().length()-1)))

                   processor = tvProcessor.getText().toString();
                tvProcessor.setText(processor + "^");
                } catch (Exception e) {
                    tvResult.setText("Error");
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                developedCounter++;
                if(developedCounter >= 5){
                    developedCounter=0;
                    tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    tvProcessor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                }else{
                    tvProcessor.setText("");
                    tvResult.setText("");
                }

            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                processor = tvProcessor.getText().toString();

                if(processor.length() > 0) {

                   /* processor = processor.replaceAll("X", "*");
                    processor = processor.replaceAll("%", "/100");

                   org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
                    rhino.setOptimizationLevel(-1);
                    String result;

                    try {
                        Scriptable scope = rhino.initStandardObjects();
                        result = rhino.evaluateString(scope, processor, "JavaScript", 1, null).toString();
                    } catch (Exception e) {
                        result = "Error";
                    }


                    if(result.substring(result.length() - 2).equals(".0")){
                        result = result.substring(0, result.length()-2);
                    }*/
                  try{
                    double l = compute(tvProcessor.getText().toString());
                    tvProcessor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                 //   tvResult.setText(result);

                    tvResult.setText(String.valueOf(l));
                    tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                  } catch (Exception e) {
                      tvResult.setText("Please, verify the spelling!");
                  }

                }
            }
        });
    }

    public void clearScreen(){

        processor = tvProcessor.getText().toString();

        developedCounter = 0;

        tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvProcessor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
    }

    public void afterClick(){
        ViewTreeObserver vto = hsvUserInput.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hsvUserInput.scrollTo(tvProcessor.getWidth(), 0);
            }
        });
    }
}