import java.io.*;
import java.util.*;

public class test {
    
		
        
    public static void main(String[] args) throws java.io.IOException {

		
		int[] d1 = new int [48];
		int width = 8;
		int height = d1.length / width;

		for (int i = 0; i < d1.length; i++) {
			d1[i] = i;
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.printf("(%2d, %2d) = %2d, ", i, j, d1[j + width * i]);
			}
			System.out.println();
		}
		System.out.println();

		// for (int i = 0; i < d1.length; i++) {
		// 	System.out.printf("%d, ", d1[j + width * i]);
		// }
		int y = 0;
		int x = 0;
		int neighborSize = 2;
		
		System.out.printf("%d\n\n", d1[x + width * y]);

		for (int i = y - neighborSize; i <= y + neighborSize; i++) {
			for (int j = x - neighborSize; j <= x + neighborSize; j++) {
				System.out.printf("(%2d, %2d) = %2d, ", i, j, d1[Math.floorMod(j , width) + width * Math.floorMod(i , height)]);
			}
			System.out.println();
		}

		int index = 0;
		int neighborWidth = 2 * neighborSize + 1;
		int neighborCount = neighborWidth * neighborWidth;
		y = index / width;
		x = index % width;
		int rand =  (new Random()).nextInt(neighborCount);
		System.out.printf("\nrand=%d, neighborCount=%d, y=%d, x=%d\n", rand, neighborCount, y, x);

		
		int y2 = rand / neighborWidth - neighborSize + index / width;
		int x2 = rand % neighborWidth - neighborSize + index % width;

		System.out.printf("\ny2=%d, x2=%d, 1D = %d\n", y2, x2, d1[Math.floorMod(x2 , width) + width * Math.floorMod(y2 , height)]);
		
        
    }
}
