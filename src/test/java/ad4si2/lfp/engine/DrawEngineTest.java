package ad4si2.lfp.engine;

import ad4si2.lfp.BaseTest;
import org.junit.Test;

import javax.inject.Inject;

public class DrawEngineTest extends BaseTest {

    @Inject
    private DrawEngine drawEngine;

    @Test
    public void getDrawTable() throws Exception {
        for (int playersCount = 2; playersCount <= 20; playersCount++) {
            int[][] table = drawEngine.getDrawTable(playersCount);

            System.out.println();
            System.out.println(playersCount);
            for (int i = 0; i < playersCount; i++) {
                for (int j = 0; j < playersCount; j++) {
                    if (i == j) {
                        System.out.print("{" + table[i][j] + "}\t");
                    } else {
                        System.out.print(table[i][j] + "\t");
                    }
                }
                System.out.println();
            }
        }
    }
}