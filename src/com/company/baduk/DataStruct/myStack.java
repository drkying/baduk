package com.company.baduk.DataStruct;

public class myStack<T> {
    private Node<T> top = null;//栈顶

    public myStack() {
        this.top = null;
    }

    //判断栈是否为空
    public boolean isEmpty() {
        if (top != null) {
            return false;
        }
        return true;
    }

    //压栈
    public boolean push(T value) {
        Node<T> node = new Node<T>(value);
        node.setNext(top);
        top = node;
        return true;
    }

    //出栈
    public T pop() {
        if (top == null) {
            return null;
        }
        T tmp = top.data;
        top = top.getNext();
        return tmp;
    }

    //取出栈顶的值
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return top.data;
    }


    class Node<T> {
        private T data;//数据
        private Node<T> next;//指向下一个节点的指针

        //初始化链表
        public Node(T data) {
            this.data = data;
        }

        //获取下一个节点
        public Node<T> getNext() {
            return this.next;
        }

        //设置下一个节点
        public void setNext(Node<T> n) {
            this.next = n;
        }

        //获取节点数据
        public T getData() {
            return this.data;
        }

        //设置节点数据
        public void setData(T d) {
            this.data = d;
        }

    }

}