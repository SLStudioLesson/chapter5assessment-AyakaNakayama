package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");
        
        // ログイン処理を呼び出す
        inputLogin();

        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(loginUser);
                        selectSubMenu();
                        break;
                    case "2":
                        inputNewInformation();
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    public void inputLogin() {
        // flgを作成し問題があればループする
        boolean flg = true;
        while (flg) {
            try {
                // メールアドレスを受け取る
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();

                // パスワードを受け取る
                System.out.print("パスワードを入力してください：");
                String password = reader.readLine();

                // userLogicからログイン処理を呼び出す
                // loginUserを更新
                loginUser = userLogic.login(email, password);
                flg = false;
            } catch (IOException e) {
                // IOExceptionをキャッチ
                e.printStackTrace();
            } catch (AppException e){
                // AppExceptionをキャッチ
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    public void inputNewInformation() {
        // flgを作成し問題があればループする
        boolean flg = true;
        while (flg) {
            try {
                // 各入力受付
                System.out.print("タスクコードを入力してください：");
                String taskCode = reader.readLine();
                // 数値でない場合はcontinue
                if (!isNumeric(taskCode)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.print("タスク名を入力してください：");
                String taskName = reader.readLine();
                // 十文字より大きい場合はcontinue
                if (taskName.length() > 10) {
                    System.out.println("タスク名は10文字以内で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.print("担当するユーザーのコードを選択してください：");
                String repUserCode = reader.readLine();
                if (!isNumeric(repUserCode)) {
                    System.out.println("ユーザーのコードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                taskLogic.save(Integer.parseInt(taskCode), taskName, Integer.parseInt(repUserCode), loginUser);
                flg = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu() {
        try {
            // 選択受付
            System.out.println("以下1~2から好きな選択肢を選んでください。");
            System.out.println("1. タスクのステータス変更, 2. メインメニューに戻る");
            System.out.print("選択肢: ");
            String select = reader.readLine();

            switch (select) {
                case "1":
                // 更新処理
                    inputChangeInformation();
                    break;
                case "2":
                    // メインメニューに戻る
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation() {
        // flgを作成し問題があればループする
        boolean flg = true;
        while (flg) {
            try {
                // 変更するタスクコード入力受付
                System.out.print("ステータスを変更するタスクコードを入力してください：");
                String taskCode = reader.readLine();
                if (!isNumeric(taskCode)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                // ステータス入力受付
                System.out.println("どのステータスに変更するか選択してください。");
                System.out.println("1. 着手中, 2. 完了");
                System.out.print("選択肢: ");
                String status = reader.readLine();

                if (!isNumeric(status)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                int statusInt = Integer.parseInt(status);
                if (!(statusInt == 1 || statusInt ==2)) {
                    System.out.println("ステータスは1・2の中から選択してください");
                    System.out.println();
                    continue;
                }

                taskLogic.changeStatus(Integer.parseInt(taskCode), statusInt, loginUser);
                flg = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
        // 数字判定
        // 1/2か判定
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    // public void inputDeleteInformation() {
    // }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    public boolean isNumeric(String inputText) {
        return inputText.chars().allMatch(c -> Character.isDigit((char) c));
    }
}