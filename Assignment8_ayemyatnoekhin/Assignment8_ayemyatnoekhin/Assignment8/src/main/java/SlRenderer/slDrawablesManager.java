package SlRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static SlUtils.slTime.getTime;
import static csc133.spot.*;
import static SlRenderer.slTilesManager.MU;
import static SlRenderer.slTilesManager.*;

import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL20.*;

public class slDrawablesManager {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private final slTilesManager board_manager;
    private final float[] vertexArray;
    private final int[] vertexIndexArray;
    int positionStride = 3;
    int colorStride = 4;
    int textureStride = 2;
    int vertexStride = (positionStride + colorStride + textureStride) * Float.BYTES;

    private int vaoID, vboID, eboID;
    final private int vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;


    public slDrawablesManager(int num_mines) {
        board_manager = new slTilesManager(num_mines);
        vertexArray = board_manager.getVertexArray();
        vertexIndexArray = board_manager.getVertexIndicesArray();
        initRendering();
    } // slDrawablesManager(int num_mines)

    private void initRendering() {
        slCamera my_camera = new slCamera(my_camera_location);
        my_camera.setOrthoProjection();

        slShaderManager testShader =
                new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");

        testShader.compile_shader();
        // TODO: Add texture manager object here:\
        slTextureManager testTexture = new slTextureManager(System.getProperty("user.dir") + "\\assets\\shaders\\FourTextures.png");
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // GL_STATIC_DRAW good for now; we can later change to dynamic vertices:
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(vertexIndexArray.length);
        elementBuffer.put(vertexIndexArray).flip();

        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(vpoIndex, positionStride, GL_FLOAT, false, vertexStride, 0);
        glEnableVertexAttribArray(vpoIndex);

        glVertexAttribPointer(vcoIndex, colorStride, GL_FLOAT, false, vertexStride,
                positionStride * Float.BYTES);
        glEnableVertexAttribArray(vcoIndex);

        // TODO: Add the vtoIndex --> "Vertex Texture Object Index" here via glVertexAttribPointer and enable it -
        // similar to other AttribPointers above.
        glVertexAttribPointer(vtoIndex, textureStride, GL_FLOAT, false, vertexStride, 28);
        glEnableVertexAttribArray(vtoIndex);

        testShader.set_shader_program();


        testShader.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        testShader.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());
    }  // void initRendering()

    public void update(int row, int col) {
        // first check if row, col >= 0 and if so, get the cell status. If status is GE --> call glDrawElements(...)
        // Else, update the polygon status change in the TilesManager, and get updated cell status.
        // If total Gold tiles == 0, expose the entire board
        // if the vertex data changed; needs updating to GPU that the vertices have changed --> need to call:

        if (row >= 0 && col >= 0) {
            int currStatus = board_manager.getCellStatus(row, col);
            if (currStatus == 0) {
                board_manager.updateForPolygonStatusChange(row, col, false);
                glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

            }


            if (currStatus == 2) {
                board_manager.updateForPolygonStatusChange(row, col, false);
                glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

                for (row = 0; row < NUM_POLY_ROWS; row++){
                    for(col = 0; col < NUM_POLY_COLS; col++) {
                        board_manager.updateForPolygonStatusChange(row, col, true);
                        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
                    }
                }
            }

        }
        glDrawElements(GL_TRIANGLES, vertexIndexArray.length, GL_UNSIGNED_INT, 0);

            //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            //glDrawElements(GL_TRIANGLES, vertexIndexArray.length, GL_UNSIGNED_INT, 0);
        } //  public void update(int row, int col)


    }
//}//slDrawablesManager