package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        // 全タスク取得
        List<Task> tasks = taskDataAccess.findAll();

        // 1タスクごとに処理
        for (Task task : tasks){
            // ステータスによって切り替え
            String status = "完了";
            if (task.getStatus() == 0) {
                status = "未着手";
            } else if (task.getStatus() == 1) {
                status = "着手中";
            }

            // loginUserとTaskのrepUserが等しいときとそうでないときで「管理者」の内容切り替え
            String admin;
            if (loginUser.getCode() == task.getRepUser().getCode()){
                admin = "あなた";
            } else {
                admin = task.getRepUser().getName();
            }

            System.out.println(task.getCode() + ". タスク名" + task.getName() + ", 担当者名:" +
                    admin + "が担当しています, ステータス:" + status);
        }
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        // repUserCodeからUserインスタンスを取得
        User repUser = userDataAccess.findByCode(repUserCode);
        // repUserCodeのUserインスタンスがnullの場合、AppExceptionをスロー
        if (repUser == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        // Taskインスタンスを作成して書き込み処理呼び出し
        Task task = new Task(code, name, 0, repUser);
        taskDataAccess.save(task);

        // Logインスタンスを作成して書き込み処理呼び出し
        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        logDataAccess.save(log);
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
        // タスクコードからタスクインスタンスを取得
        Task task = taskDataAccess.findByCode(code);
        // タスクインスタンスがnullの場合AppException
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }

        // 引数のstatusとタスクインスタンスのstatusが等しいときAppException
        if (status == task.getStatus()) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        // タスクを更新
        task.setStatus(status);
        taskDataAccess.update(task);
        // ログを更新
        logDataAccess.save(new Log(code, loginUser.getCode(), status, LocalDate.now()));
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
        // 全タスクのリストを取得
        // タスクごとに処理を実行
    // }
}