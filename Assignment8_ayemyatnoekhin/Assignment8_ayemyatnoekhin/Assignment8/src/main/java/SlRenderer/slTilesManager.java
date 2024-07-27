package SlRenderer;

import org.joml.Vector2i;

import javax.swing.*;
import java.util.Random;

import static csc133.spot.*;

public class slTilesManager {
    private float[] verticesArray;
    private int[] vertexIndicesArray;

    private final int vps = 4; // Vertices Per Square
    private final int fpv = 9;  // Vertices Per Vertex
    private final int ips = 6; // Indices per square

    // 0 <-- gold & unexposed; GU, 1 <-- gold & exposed; GE,
    // 2 <-- mine & unexposed; MU, 3 <-- mine & exposed; ME.
    public static final int GU = 0;
    public static final int GE = 1;
    public static final int MU = 2;
    public static final int ME = 3;
    // Precomputed Texture Coordinates for the above states - tied to how textures are tiled:
    //umin, vmin, umax, vmax
    private static final float[] GUTC = {0.5f, 0.5f, 1.0f, 0.0f};
    private static final float[] MUTC = GUTC;
    private static final float[] GETC = {0.0f, 1.0f, 0.5f, 0.5f};
    private static final float[] METC = {0.5f, 1.0f, 1.0f, 0.5f};

    private int[] cellStatusArray;
    private int[] cellStats;  // {exposed gold - unexposed gold - mines} - in that order.
    // < 0 --> game in progress, 0 --> ended on gold, 1 --> ended on a mine, 2 --> game ended.
    private int boardDisplayStatus = -1;
    private int num_mines;

    float ZMIN = 0.0f; // z coordinate for all polygons

    public slTilesManager(int total_mines) {

        num_mines = total_mines;

        cellStatusArray = new int[NUM_POLY_COLS * NUM_POLY_ROWS];
        cellStats = new int[] {0, NUM_POLY_COLS * NUM_POLY_ROWS - total_mines, total_mines};

        for (int ii = 0; ii < cellStatusArray.length; ++ii) {
            cellStatusArray[ii] = GU;
        }

        int cur_mines = 0, cur_index = -1;
        Random my_rand = new Random();
        while (cur_mines < num_mines) {
            cur_index = my_rand.nextInt(cellStatusArray.length);
            if (cellStatusArray[cur_index] != MU) {
                cellStatusArray[cur_index] = MU;
                ++cur_mines;
            }
        }

        setVertexArray();
        setVertexIndicesArray();
        printStats();
        updateStatusArrayToDisplayAll();
    }  //  public slGeometryManager(int num_mines)

    // Call fillSquarecoordinates for each cell array
    private void setVertexArray() {
        float xmin = POLY_PADDING;
        float xmax = xmin + SQUARE_LENGTH;
        float ymax = WIN_HEIGHT - POLY_PADDING;
        float ymin = ymax - SQUARE_LENGTH;
        verticesArray = new float[(NUM_POLY_ROWS * NUM_POLY_COLS) * vps * fpv];
        int index = 0;
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                if (getCellStatus(row, col) == 0) {
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GUTC[0]; //uvmin
                    verticesArray[index++] = GUTC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GUTC[2]; //uvmax
                    verticesArray[index++] = GUTC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GUTC[2]; //uvmax
                    verticesArray[index++] = GUTC[3]; //uvmax
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GUTC[0]; //uvmin
                    verticesArray[index++] = GUTC[3]; //uvmax
                }
                if (getCellStatus(row, col) == 1) {
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GETC[0]; //uvmin
                    verticesArray[index++] = GETC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GETC[2]; //uvmax
                    verticesArray[index++] = GETC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GETC[2]; //uvmax
                    verticesArray[index++] = GETC[3]; //uvmax
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = GETC[0]; //uvmin
                    verticesArray[index++] = GETC[3]; //uvmax
                }
                if (getCellStatus(row, col) == 2) {
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = MUTC[0]; //uvmin
                    verticesArray[index++] = MUTC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = MUTC[2]; //uvmax
                    verticesArray[index++] = MUTC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = MUTC[2]; //uvmax
                    verticesArray[index++] = MUTC[3]; //uvmax
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = MUTC[0]; //uvmin
                    verticesArray[index++] = MUTC[3]; //uvmax
                }
                if (getCellStatus(row, col) == 3) {
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = METC[0]; //uvmin
                    verticesArray[index++] = METC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymin;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = METC[2]; //uvmax
                    verticesArray[index++] = METC[1]; //uvmin
                    verticesArray[index++] = xmax;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = METC[2]; //uvmax
                    verticesArray[index++] = METC[3]; //uvmax
                    verticesArray[index++] = xmin;
                    verticesArray[index++] = ymax;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 0.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = 1.0f;
                    verticesArray[index++] = METC[0]; //uvmin
                    verticesArray[index++] = METC[3]; //uvmax
                }
                xmin = xmax + POLY_PADDING;
                xmax = xmin + SQUARE_LENGTH;
            } //End of for loop with columns
            xmin = POLY_PADDING;
            xmax = xmin + SQUARE_LENGTH;
            ymax = ymin - POLY_PADDING;
            ymin = ymax - SQUARE_LENGTH;
        } //End of for loop with rows
    }
    // Given a index, row, column, fill up the vertices of the square. "index" is the index
    // to the vert_array, a multiple of vps * fpv
    private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array) {


    }  //  private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array)

    public void setVertexIndicesArray() {
        vertexIndicesArray = new int[NUM_POLY_ROWS*NUM_POLY_COLS*ips];
        int v_index = 0;
        int my_index = 0;
        while (my_index < vertexIndicesArray.length){
            //Start by drawing the bottom triangle
            vertexIndicesArray[my_index++] = v_index;
            vertexIndicesArray[my_index++] = v_index+1;
            vertexIndicesArray[my_index++] = v_index+2;
            //Finish by drawing the top triangle
            vertexIndicesArray[my_index++] = v_index;
            vertexIndicesArray[my_index++] = v_index+2;
            vertexIndicesArray[my_index++] = v_index+3;
            v_index += vps;
        } //End of while
    }  //  public int[] setVertexIndicesArray(...)

    public void updateForPolygonStatusChange(int row, int col, boolean printStats) {
        //locate the index to the verticesArray:
        int fps = vps * fpv; //Floats Per Square
        int first_offset = 7; // first texture coord offset;
        int tc_offset = 8;  // subsequent texture coord offsets;
        int indx = (row * NUM_POLY_COLS + col) * fps + first_offset;
        float umin = GETC[0], vmin = GETC[1], umax = GETC[2], vmax = GETC[3];
        int old_state = cellStatusArray[row * NUM_POLY_COLS + col];
        int new_state = -1;
        if (old_state == GU ) {
            new_state = GE;
            ++cellStats[0];
            --cellStats[1];
            if (printStats && boardDisplayStatus < 0) {
                printStats();
            }
            // tex coords set to this by default - no need to update
        } else if (old_state == MU) {
            new_state = ME;
        } else {
            return;
        }

        if (new_state == ME) {
            umin = METC[0]; umax = METC[1]; vmin = METC[2]; vmax = METC[3];
        }
        cellStatusArray[row * NUM_POLY_COLS + col] = new_state;

        verticesArray[indx++] = umin;
        verticesArray[indx] = vmin;
        indx += tc_offset;
        verticesArray[indx++] = umax;
        verticesArray[indx] = vmin;
        indx += tc_offset;
        verticesArray[indx++] = umax;
        verticesArray[indx] = vmax;
        indx += tc_offset;
        verticesArray[indx++] = umin;
        verticesArray[indx] = vmax;
    }

    private void printStats() {
        System.out.println("Unexposed: " + cellStats[1] + "\nExposed: " + cellStats[0] + "\nMines Unexposed: " + cellStats[2]);
    }

    // status can be GE, ME, GU, MU
    public void setCellStatus(int row, int col, int status) {
        if (row < 0 || row >= NUM_POLY_ROWS || col < 0 || col >= NUM_POLY_COLS) {
            // Invalid row or column
            return;
        }
        cellStatusArray[row * NUM_POLY_COLS + col] = status;

    }  //  public void setCellStatus(int row, int col, int status)

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    public void updateStatusArrayToDisplayAll() {
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                int index = row * NUM_POLY_COLS + col;
                int status = cellStatusArray[index];
                if (status == 2) {
                    System.out.print("2 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();


        }
    }

    public float[] getVertexArray() {
        return verticesArray;
    }

    public int[] getVertexIndicesArray() {
        return vertexIndicesArray;
    }

    public void printMineSweeperArray() {
        for (int row = 0; row < NUM_POLY_ROWS; row++){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                int currCellStatus = getCellStatus(row, col);
                if (currCellStatus == 0){
                    System.out.print("0 ");
                } else {
                    System.out.print("2 ");
                }
            }
            System.out.println();
        }




    }  //  public void printMineSweeperArray()

    public int[] getCellStats() {
        return cellStats;
    }

    // returns (-1, -1) if a cell was not selected; else computes the row and column
    // corresponding to the window coordinates and returns the updated retVec
    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        Vector2i retVec = new Vector2i(-1, -1);
        int R = (int)((xpos - POLY_OFFSET) / (SQUARE_LENGTH + POLY_PADDING)); //Column
        int H = (int)((ypos - POLY_OFFSET) / (SQUARE_LENGTH + POLY_PADDING)); //Row
        float xmin_r = POLY_OFFSET + R * (SQUARE_LENGTH + POLY_PADDING);
        float xmax_r = xmin_r + SQUARE_LENGTH;
        float ymin_r = POLY_OFFSET + H * (SQUARE_LENGTH + POLY_PADDING);
        float ymax_r = ymin_r + SQUARE_LENGTH;
        if ((xmin_r <= xpos && xpos <= xmax_r) && (ymin_r <= ypos && ypos <= ymax_r)){
            System.out.println("This is a square " + retVec.set(NUM_POLY_ROWS-1-H, R));




            return retVec.set(NUM_POLY_ROWS-1-H, R);
        } else {
            return retVec;
        } // End of if-else
    } // public static Vector2i getRowColFromXY(float xpos, float ypos)

}  //  public class slGeometryManager