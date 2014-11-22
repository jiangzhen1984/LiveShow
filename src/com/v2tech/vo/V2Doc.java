package com.v2tech.vo;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

public class V2Doc {

	public static final int DOC_TYPE_IMAGE = 1;
	public static final int DOC_TYPE_BLANK_BOARD = 2;

	protected String id;
	protected Group mGroup;
	protected int mBType;
	protected User mSharedUser;
	protected int mDocType;
	protected PageArray pageArray;
	protected String mDocName;

	protected int currentPageNo = 1;

	public static class PageArray {
		String docId;
		SparseArray<Page> page;

		public PageArray(Page[] pr) {
			page = new SparseArray<Page>();
			addPages(pr);
		}

		public PageArray(SparseArray<Page> page) {
			this.page = page;
		}

		public PageArray() {
			page = new SparseArray<Page>();
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public void addPages(Page[] pr) {
			for (int i = 0; pr != null && i < pr.length; i++) {
				page.put(i, pr[i]);
			}
		}

		/**
		 * Update cache page
		 * @param p
		 */
		public void addPage(Page p) {
			Page existP = page.get(p.getNo());
			if (existP != null) {
				existP.update(p);
			} else {
				page.put(p.getNo(), p);
				
			}
		}
		
		public void updatePage(Page p) {
			page.put(p.getNo(), p);
		}

		public Page getPage(int no) {
			return page.get(no);
		}
		
		
		public int getPageSize() {
			return this.page.size();
		}
		
		public Page getPageByIndex(int index){
			return this.page.valueAt(index);
		}
		
		
		public void update(PageArray pa) {
			if (pa == null) {
				return;
			}
			for (int i = 0; i < pa.getPageSize(); i++) {
				Page newP = pa.getPageByIndex(i);
				Page oldP = getPage(newP.no);
				if (oldP == null) {
					updatePage(newP);
				} else {
					oldP.update(newP);
				}
			}
		}

	}

	public static class Page {
		int no;
		String docId;
		String filePath;
		List<V2ShapeMeta> vsMeta;
		
		protected Page() {
			
		}

		public Page(int no, String docId, String filePath) {
			this.no = no;
			this.docId = docId;
			this.filePath = filePath;
			vsMeta = new ArrayList<V2ShapeMeta>();
		}

		public Page(int no, String docId, String filePath,
				List<V2ShapeMeta> vsMeta) {
			this.no = no;
			this.docId = docId;
			this.filePath = filePath;
			this.vsMeta = vsMeta;
			if (this.vsMeta == null) {
				this.vsMeta = new ArrayList<V2ShapeMeta>();
			}
		}

		public int getNo() {
			return no;
		}

		public void setNo(int no) {
			this.no = no;
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public List<V2ShapeMeta> getVsMeta() {
			return vsMeta;
		}

		public void setVsMeta(List<V2ShapeMeta> vsMeta) {
			this.vsMeta = vsMeta;
		}

		public void addMeta(V2ShapeMeta meta) {
			this.vsMeta.add(meta);
		}
		
		
		public void update(Page p) {
			if (p == null) {
				return;
			}
			
			if (p.no > 0) {
				this.no = p.no;
			}
			if (p.docId != null) {
				this.docId = p.docId;
			}
			if (p.filePath != null) {
				this.filePath = p.filePath;
			}
			if (this.vsMeta == null) {
				this.vsMeta = new ArrayList<V2ShapeMeta>();
			}
			this.vsMeta.addAll(p.vsMeta);
			
		}

	}
	
	
	public static class BlankBorad extends Page {
		public BlankBorad(int no, String docId,
				List<V2ShapeMeta> vsMeta) {
			
			this.no = no;
			this.docId = docId;
			this.vsMeta = vsMeta;
			if (this.vsMeta == null) {
				this.vsMeta = new ArrayList<V2ShapeMeta>();
			}
		}
	}

	public V2Doc(String id, String docName, Group mGroup, int mBType,
			User mSharedUser) {
		super();
		this.id = id;
		this.mDocName = docName;
		this.mGroup = mGroup;
		this.mBType = mBType;
		this.mSharedUser = mSharedUser;
		this.pageArray = new PageArray();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocName() {
		return mDocName;
	}

	public void setDocName(String docName) {
		this.mDocName = docName;
	}

	public Group getGroup() {
		return mGroup;
	}

	public void setGroup(Group mGroup) {
		this.mGroup = mGroup;
	}

	public int getBType() {
		return mBType;
	}

	public void setBType(int mBType) {
		this.mBType = mBType;
	}

	public User getSharedUser() {
		return mSharedUser;
	}

	public void setSharedUser(User mSharedUser) {
		this.mSharedUser = mSharedUser;
	}

	public int getDocType() {
		return mDocType;
	}

	public void setDocType(int mDocType) {
		this.mDocType = mDocType;
	}

	public void addPage(Page p) {
		if (p == null) {
			throw new NullPointerException(" page is null");
		}
		pageArray.addPage(p);
	}

	public Page findPage(int no) {
		return pageArray.getPage(no);
	}

	public int getActivatePageNo() {
		return this.currentPageNo;
	}

	public Page getActivatePage() {
		return pageArray.getPage(currentPageNo);
	}

	public void setActivatePageNo(int no) {
		this.currentPageNo = no;
	}

	public int getPageSize() {
		return pageArray.getPageSize();
	}

	/**
	 * Get page according page number.
	 * @param no from 1 start
	 * @return
	 */
	public Page getPage(int no) {
		if (no <= 0 || no > pageArray.getPageSize()) {
			throw new IndexOutOfBoundsException("Page no is incorrect ");
		}
		return pageArray.getPage(no);
	}
	/**
	 * Update existed page array. Will ignore if parameter is null
	 * @param pa
	 */
	public void updatePageArray(PageArray pa) {
		if (pa == null) {
			return;
		}
		this.pageArray.update(pa);
	}
	
	
	
	/**
	 *  Update current doc. Will ignore if parameter is null
	 * @param doc
	 */
	public void updateDoc(V2Doc doc) {
		if (doc.getDocName() != null) {
			this.mDocName = doc.getDocName();
		}
		if (doc.getGroup() != null) {
			this.mGroup = doc.getGroup();
		}
		if (doc.getSharedUser() != null) {
			this.mSharedUser = doc.getSharedUser();
		}
		this.mBType = doc.getBType();
		this.pageArray.update(doc.pageArray);
	}
}
