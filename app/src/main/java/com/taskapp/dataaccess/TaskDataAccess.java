package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        // Userインスタンスのリストを作成
        List<Task> tasks = new ArrayList<>();

        // ファイルを開く
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // ヘッダーはスキップ
            reader.readLine();
            // 一行ずつ読み込む
            String line;
            while ((line = reader.readLine()) != null) {
                // 一行をカンマで分割
                String[] values = line.split(",");

                // データ数が足りない場合スキップ
                if (values.length != 4) continue;

                // データからTaskインスタンスを作成
                int code = Integer.parseInt(values[0]);
                String name = values[1];
                int status = Integer.parseInt(values[2]);
                int repUserCode = Integer.parseInt(values[3]);
                User repUser = userDataAccess.findByCode(repUserCode);
                Task task = new Task(code, name, status, repUser);

                // 作成したTaskインスタンスをリストに詰める
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        // ファイルを書き込むために開く（一行追加）
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Taskインスタンスから文字列を作成
            String line = task.getCode() + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getRepUser().getCode();

            // 改行後に一行追加
            writer.newLine();
            writer.write(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        Task task = null;
        // ファイル読み込み
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // ヘッダーを飛ばす
            reader.readLine();
            // 一行ずつ読み込む
            String line;
            while((line = reader.readLine()) != null){
                String[] values = line.split(",");

                // データ数が足りていない場合はスキップ
                if (values.length != 4) continue;

                // コードが一致していない場合はスキップ
                int taskCode = Integer.parseInt(values[0]);
                if (code != taskCode) continue;

                // データからTaskインスタンスを作成
                User repUser = userDataAccess.findByCode(Integer.parseInt(values[3]));
                task = new Task(taskCode, values[1],
                        Integer.parseInt(values[2]), repUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return task;
    }

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        // 全タスク取得
        List<Task> tasks = findAll();

        // 書き込み処理呼び出し
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // ヘッダー書き出し
            writer.write("Code,Name,Status,Rep_User_Code");
            for (Task task : tasks){
                // タスクコードがupdateTaskと等しいときupdateTask、それ以外はそのままで行作成
                String line;
                if (updateTask.getCode() == task.getCode()) {
                    line = createLine(updateTask);
                } else {
                    line = createLine(task);
                }

                // 書き込み
                writer.newLine();
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
        // タスクを全件取得
        // ファイルの書き込み処理
    //     try () {
            // ヘッダー追加
            // タスクのリストごとに処理
            // 引数のCodeとTaskのCodeが一致した場合処理をスキップ
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        return task.getCode() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getRepUser().getCode();
    }
}