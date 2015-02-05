package com.wyp.materialqqlite.qqclient.task;

public class Task implements Runnable {
	protected TaskManager m_taskMgr;
	protected String m_strTaskName;
	protected boolean m_bCancel;
	protected boolean m_bRunning;
		
	public Task(String strTaskName) {
		m_taskMgr = null;
		m_strTaskName = strTaskName;
		m_bCancel = false;
		m_bRunning = false;
	}
	
	public TaskManager getTaskManager() {
		return m_taskMgr;
	}
	
	public void setTaskManager(TaskManager taskMgr) {
		m_taskMgr = taskMgr;
	}

	public String getTaskName() {
		return m_strTaskName;
	}
	
	public void setTaskName(String strTaskName) {
		m_strTaskName = strTaskName;
	}
	
	public void cancelTask() {
		m_bCancel = true;
	}
	
	public boolean isRunning() {
		return m_bRunning;
	}
		
    @Override
    public void run() {
    	if (m_bCancel)
    		return;
    	
    	m_bRunning = true;
    	doTask();
    	m_bRunning = false;
    	
    	if (m_taskMgr != null)
    		m_taskMgr.delTask(m_strTaskName);
    	
    	m_bCancel = false;
    }
    
    public void doTask() {
    	
    }
}
