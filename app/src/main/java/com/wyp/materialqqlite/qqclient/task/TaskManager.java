package com.wyp.materialqqlite.qqclient.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;

public class TaskManager {
	private ExecutorService m_ThreadPool = null;
	private HashMap<String, Task> m_mapTask = null;
	private static final int THREAD_COUNT = 10;
	
	synchronized public void init(int nThreadNums) {
		if (1 == nThreadNums)
			m_ThreadPool = Executors.newSingleThreadExecutor();
		else if (-1 == nThreadNums)
			m_ThreadPool = Executors.newCachedThreadPool();
		else if (0 == nThreadNums)
			m_ThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		else
			m_ThreadPool = Executors.newFixedThreadPool(nThreadNums);
		
		m_mapTask = new HashMap<String, Task>();
	}
	
	synchronized public void shutdown() {
		m_ThreadPool.shutdown();
		for (Task task : m_mapTask.values())
        {
            if (task != null)
            	task.cancelTask();
        }
		m_mapTask.clear();
	}
	
	synchronized public boolean addTask(Task task) {
		if (m_ThreadPool.isShutdown())
			return false;
		
		if (m_mapTask.get(task.getTaskName()) != null)
			return false;
		
		task.setTaskManager(this);
		m_mapTask.put(task.getTaskName(), task);
		m_ThreadPool.execute(task);
		return true;
	}
	
	synchronized public Task findTask(String strTaskName) {
		return m_mapTask.get(strTaskName);
	}
	
	synchronized public void delTask(String strTaskName) {
		Task task = m_mapTask.get(strTaskName);
		if (task != null) {
			task.cancelTask();
			m_mapTask.remove(strTaskName);
		}
	}
	
	synchronized public void delAllTask() {
		for (Task task : m_mapTask.values())
        {
            if (task != null)
            	task.cancelTask();
        }
		m_mapTask.clear();
	}
}
