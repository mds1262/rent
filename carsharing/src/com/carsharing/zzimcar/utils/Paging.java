package com.carsharing.zzimcar.utils;

public class Paging {

	int maxPost;
	int firstPageNo;
	int prevPageNo;
	int startPageNo;
	int currentPageNo;
	int endPageNo;
	int nextPageNo;
	int finalPageNo;
	int numberOfRecords;
	int sizeOfPage;
	int offset;

	public Paging() {
	}

	public int getmaxPost() {
		return maxPost;
	}

	public void setmaxPost(int maxPost) {
		this.maxPost = maxPost;
	}

	public int getFirstPageNo() {
		return firstPageNo;
	}

	public void setFirstPageNo(int firstPageNo) {
		this.firstPageNo = firstPageNo;
	}

	public int getPrevPageNo() {
		return prevPageNo;
	}

	public void setPrevPageNo(int prevPageNo) {
		this.prevPageNo = prevPageNo;
	}

	public int getStartPageNo() {
		return startPageNo;
	}

	public void setStartPageNo(int startPageNo) {
		this.startPageNo = startPageNo;
	}

	public int getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}

	public int getEndPageNo() {
		return endPageNo;
	}

	public void setEndPageNo(int endPageNo) {
		this.endPageNo = endPageNo;
	}

	public int getNextPageNo() {
		return nextPageNo;
	}

	public void setNextPageNo(int nextPageNo) {
		this.nextPageNo = nextPageNo;
	}

	public int getFinalPageNo() {
		return finalPageNo;
	}

	public void setFinalPageNo(int finalPageNo) {
		this.finalPageNo = finalPageNo;
	}

	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void makePaging() {

		if (numberOfRecords == 0)
			return;

		if (currentPageNo == 0)
			setCurrentPageNo(1);

		if (maxPost == 0)
			setmaxPost(10);

		int finalPage = (numberOfRecords + (maxPost - 1)) / maxPost;

		if (currentPageNo > finalPage)
			setCurrentPageNo(finalPage);

		if (currentPageNo < 0)
			currentPageNo = 1;

		boolean isNowFirst = currentPageNo == 1 ? true : false;
		boolean isNowFinal = currentPageNo == finalPage ? true : false;

		int startPage = ((currentPageNo - 1) / sizeOfPage) * sizeOfPage + 1;
		int endPage = startPage + sizeOfPage - 1;

		if (endPage > finalPage)
			endPage = finalPage;

		setFirstPageNo(1);

		if (!isNowFirst)
			setPrevPageNo(((startPage - 1) < 1 ? 1 : (startPage - 1)));

		setStartPageNo(startPage);
		setEndPageNo(endPage);

		if (!isNowFinal)
			setNextPageNo(((endPage + 1 > finalPage ? finalPage : (endPage + 1))));

		setFinalPageNo(finalPage);

	}

	public Paging pagingUtil(String currentPn) throws Exception {

		int currentPageNo = 1;

		if (currentPn != null) {
			currentPageNo = Integer.parseInt(currentPn);
		}

		this.currentPageNo = (currentPageNo != 0) ? currentPageNo : 1;
		this.sizeOfPage = 10; 
		this.maxPost = (maxPost != 0) ? maxPost : 10;
		this.offset = (getCurrentPageNo() - 1) * getmaxPost();

		makePaging();

		return this;
	}
}