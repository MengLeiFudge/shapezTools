package shapez;

import shapez.calculate.GetAllShapes;
import shapez.check_floating.Check;
import shapez.puzzle.GetAllPuzzles;
import shapez.puzzle.ShowOnePuzzle;
import shapez.puzzle.SolvePuzzle;

import static shapez.SettingsAndUtils.sc;

/**
 * 这是一个 Shapez 的处理程序.
 * <p>
 * 它主要
 *
 * @author MengLeiFudge
 */
public class Main {
    public static void main(String[] args) {
        while (true) {
            System.out.println("※ 该项目仅供学习研究之用 ※");
            System.out.println("选择功能：");
            System.out.println("1.获取所有可合成图形及最短路径数据库");
            System.out.println("2.验证指定悬空拆分算法");
            System.out.println("3.查询指定ID或短代码对应Puzzle");
            System.out.println("4.获取所有Puzzle并排序");
            System.out.println("5.直接对所有Puzzle排序");
            System.out.println("6.输出指定谜题的解");
            System.out.println("0.结束");
            switch (sc.nextLine()) {
                case "1" -> new GetAllShapes().process();
                case "2" -> new Check().process();
                case "3" -> new ShowOnePuzzle().process();
                case "4" -> new GetAllPuzzles().process(true);
                case "5" -> new GetAllPuzzles().process(false);
                case "6" -> new SolvePuzzle().process();
                case "0" -> {
                    System.out.println("喜欢本项目的话，请给萌泪点个star！");
                    return;
                }
                default -> {
                    System.out.println("输入有误！");
                    System.out.println();
                    System.out.println();
                }
            }
            System.out.println();
        }
    }
}

