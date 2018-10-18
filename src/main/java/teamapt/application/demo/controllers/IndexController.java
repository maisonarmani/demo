package teamapt.application.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import teamapt.application.demo.domains.Base;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Controller
public class IndexController {
    @Autowired
    Base base;
    @RequestMapping({"/"})
    public String getIndex(Model model){
        model.addAttribute("base", base.getClass());
        return "index";
    }

    class Solver {
        final int N;
        final float[][] data;
        final CyclicBarrier barrier;

        class Worker implements Runnable {
            int myRow;
            Worker(int row) { myRow = row; }
            public void run() {
                while (!done(myRow)) {
                    processRow(myRow);

                    try {
                        barrier.await();
                    } catch (InterruptedException ex) {
                        return;
                    } catch (BrokenBarrierException ex) {
                        return;
                    }
                }
            }
        }

        public Solver(float[][] matrix) {
            data = matrix;
            N = matrix.length;
            barrier = new CyclicBarrier(N,
                    new Runnable() {
                        public void run() {
                            mergeRows();
                        }
                    });
            for (int i = 0; i < N; ++i)
                new Thread(new Worker(i)).start();


            this.waitUntilDone();
        }

        boolean done(int myRow){
            return myRow < 5;
        }

        void waitUntilDone(){
            System.out.println("Waiting until done");
        }

        void processRow(int myRow){
            myRow =+ 1;
            System.out.println("Processing row");
        }


        void mergeRows(){
            System.out.println("Merging rows");
        }
    }





}
