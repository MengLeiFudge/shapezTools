package shapez.puzzle;

import com.alibaba.fastjson2.JSONObject;

import static shapez.SettingsAndUtils.getPuzzleJson;


/**
 * 输入一个谜题的短代码/id/json文件，输出这个谜题的解，并将解存于json文件中.
 *
 * @author MengLeiFudge
 */
public class SolvePuzzle {

    public void process() {

    }

    public void solve(int id) {
        JSONObject obj = getPuzzleJson(id);
        if (obj == null) {
            System.out.println(id + " is null");
            return;
        }
        if (obj.containsKey("error")) {
            return;
        }
        Puzzle puzzle = Puzzle.getPuzzleInstanceByJson(obj);
        // 无法使用的输出改成方块
        // 改成左下角为坐标原点，把y反过来。也就是把puzzle放在第一象限，这样更方便阅读和写代码
        // 双染应该是最麻烦的了，出料类型是两个进料都有；
        // 如果用分离器，分离器前端为双染左边的，注意后续产物类型；
        // 现在看来，先不用双染，如果算不出来再用双染，这样行不行？
        // 切割两边路径长度不一样导致的堵料暂时不考虑，双切四切只要速率一样就行，哪怕一起堵料
        // 找到所有路径连通的情况，注意源输出可用可不用，
        // 但是用了的必须“有归宿”。
        // 当所有解密者放的部件均有输入和输出，此时即可判断产物。
        // 最后判断产物，产物对吗？速率够不够？
        // 如果剪切产物传送带相差超过3个格子或以上，判断少的那边有没有传送带，有则ok
        // 先获取所有装置可能的位置，再选取最少的进行遍历。
    }

}
