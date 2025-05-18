package com.nuvoled.ndi;

class testrot {

    // Function to rotate the matrix by 90 degrees clockwise
    static void rotate90(int[][] mat) {
        //input int[y][x] -> int[4][3]

        int resY = 4;
        int resX = 3;

        // int[x][y]
        int[][] res = new int[resX][resY];

        for (int i = 0; i < resY; i++) {
            for (int j = 0; j < resX; j++) {
                System.out.println(i + ", " + j + " -> " + j + ", " + (resY - i - 1));
                res[j][resY - i - 1] = mat[i][j];
            }
        }

        for (int[] row : res) {
            for (int x : row) {
                System.out.print(x + " ");
            }
            System.out.println();
        }

    }


    public static void main(String[] args) {
        int[][] mat = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
                {10, 11, 12}
        };

        rotate90(mat);

    }
}