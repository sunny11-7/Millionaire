package com.sxt;
import java.util.ArrayList;
import java.util.Random;

public class ew_millionaire {

	// 获取小于等于指定数的素数数组
    public static ArrayList<Integer> getPrimeArray(int max) {
        ArrayList<Integer> primeArray = new ArrayList<>();
        for (int i = 2; i < max; i++) {
            if (isPrime(i)) {
                primeArray.add(i);
            }
        }
        return primeArray;
    }

    // 判断是否为素数
    public static boolean isPrime(int num) {
        if (num == 1) {
            throw new IllegalArgumentException("1既不是素数也不是合数");
        }
        for (int i = 2; i <= Math.floor(Math.sqrt(num)); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    // 找出一个指定范围内与n互质的整数e
    public static int findPublicKey(int n, int maxNum) {
        Random random = new Random();
        while (true) {
            int e = random.nextInt(maxNum) + 1;
            if (gcd(e, n) == 1) {
                return e;
            }
        }
    }

    // 求两个数的最大公约数
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // 根据e*d mod s = 1,找出d
    public static int findPrivateKey(int e, int s) {
        for (int d = 1; d < 100000000; d++) {
            if ((e * d) % s == 1) {
                return d;
            }
        }
        return -1; // handle if d is not found
    }

    // 生成公钥和私钥
    public static int[] buildKey() {
        ArrayList<Integer> primeArray = getPrimeArray(50);
        Random random = new Random();
        int p = primeArray.get(random.nextInt(primeArray.size()));
        int q;
        do {
            q = primeArray.get(random.nextInt(primeArray.size()));
        } while (p == q);
        System.out.println("随机生成两个素数p和q. p=" + p + ", q=" + q);
        int n = p * q;
        int s = (p - 1) * (q - 1);
        int e = findPublicKey(s, 100);
        System.out.println("根据e和(p-1)*(q-1)互质得到: e=" + e);
        int d = findPrivateKey(e, s);
        System.out.println("根据 e*d 模 (p-1)*(q-1) 等于 1 得到 d=" + d);
        System.out.println("公钥:   n=" + n + ", e=" + e);
        System.out.println("私钥:   n=" + n + ", d=" + d);
        return new int[] {n, e, d};
    }
    
    public static int modPow(int base, int exponent, int modulus) {
        if (modulus == 1) return 0;
        int result = 1;
        base = base % modulus;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = (result * base) % modulus;
            }
            exponent = exponent >> 1;
            base = (base * base) % modulus;
        }
        return result;
    }
    // 加密
    public static int rsaEncrypt(int content, int[] ned) {
        // 密文B = 明文A的e次方 模 n， ned为公钥
        // content就是明文A，ned[1]是e， ned[0]是n
        return modPow(content, ned[1], ned[0]);
    }

    // 解密
    public static int rsaDecrypt(int encryptResult, int[] ned) {
        // 明文C = 密文B的d次方 模 n， ned为私钥
        // encryptResult就是密文, ned[1]是d, ned[0]是n
        return modPow(encryptResult, ned[1], ned[0]);
    }

    public static void main(String[] args) {
        int[] pbvk = buildKey();
        int[] pbk = {pbvk[0], pbvk[1]}; // 公钥 (n, e)
        int[] pvk = {pbvk[0], pbvk[2]}; // 私钥 (n, d)
        //System.out.println("hhh: " + pbvk[0]);
        Random random = new Random();
        int i = random.nextInt(10) + 1;
        int j = random.nextInt(10) + 1;
        System.out.println("==============================================");
        System.out.println("王有i = " + i + "亿, 李有j = " + j + "亿");
        if(pbk[0]-50<0) {
        	 System.out.println("请再次运行！");
        	 System.exit(0);
        }
        int x = random.nextInt(pbk[0]-50) + 50; // assert(x < N) | N=p*q
        System.out.println("随机选取的大整数x: " + x);
        int K = rsaEncrypt(x, pbk);
        System.out.println("大整数加密后得密文K: " + K);
        int c = K - j;
        System.out.println("王收到数字c: " + c);

        ArrayList<Integer> cList = new ArrayList<>();
        for (int k = 1; k <= 10; k++) {
            int t = rsaDecrypt(c + k, pvk);
            cList.add(t);
            //cList.add(k-1, t);
        }
        System.out.println("对c+1到c+10进行解密: " + cList);

        int p;
        do {
            p = random.nextInt(x - 30) + 30;
        } while (!isPrime(p));// assert(p<x)
        ArrayList<Integer> dList = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            dList.add(cList.get(k) % p);
        }
        System.out.println("p的值为: " + p);
        System.out.println("除以p后的余数为: " + dList);

        //dList.set(i-1, dList.get(i-1) + 1);
        for (int k = i-1; k < 10; k++) {
            dList.set(k, dList.get(k) + 1);
        }
        System.out.println("前i-1位数字不动,第i位及之后数字+1: " + dList);
        System.out.println("第j个数字为: " + dList.get(j - 1));
        System.out.println("x mod p为: " + (x % p));
        if (dList.get(j - 1).equals(x % p)) {
            System.out.println("i>j,即王比李有钱或一样有钱。");
            if (i - j >= 0) {
                System.out.println("验证成功");
            } else {
                System.out.println("代码存在错误");
            }
        } /*else if (dList.get(j - 1).equals((x % p) + 1)) {
            System.out.println("i=j,即王和李一样有钱。");
        } */else {
            System.out.println("i<j,即李比王有钱");
            if (i - j < 0) {
                System.out.println("验证成功");
            } else {
                System.out.println("代码存在错误");
            }
        }
    }
}
