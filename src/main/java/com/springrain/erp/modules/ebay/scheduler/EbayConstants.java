package com.springrain.erp.modules.ebay.scheduler;

public interface EbayConstants {

	static final String EBAYTOKEN = "AgAAAA**AQAAAA**aAAAAA**pC9QWg**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AFkICnCZSKqQydj6x9nY+seQ**zNUBAA**AAMAAA**mtYEOfMAvKWxDDJe1svbKeGP4iA8pP305p1ZszYRWP4FpRz/tn0uCvnTPb53DMP72CtdBtd6Z8CUCXHcA2xlsVZhoXG/pAHb46YTfVDWB227UocITdpVrwYNJGojKh9h8n0xg/eSjS3fprYEics8Y0zTEkB/porp2cKCcxnrohDlGaCQS2FALIzfAhE7WXxrxsMHDYIo6BS67PltM4JpZ//2cnZSV0cW5eUTsbuL06IlhmXGsnLG2vABQtK3UFwTUIqXZRWX4bz/brdfAr9jxr12Nz362iMfzPdSGEF/DdhQavgGXC7psWMG1e6lGpY646DbyPHQtk6Vj3gzha82XwvHt3xxOx3us970+5bXIsvR4ufWn//6AACihocuQtkWTijm1dlYm+4dvaRMR+ve65UqxfuhXuln2AbF0FU8NwRzTBjYdIeg3k4Xpl0tDf5deLiEQqAnSnB/fbP3jnJ3AP6Rns9ZwYlxg6kKa2vIYMzAKSVrIDx3JbW47Li7WVrrimrMoWLrEubBIxFZ7g7oH51iB+sFq4MFb3JpwkuWOPG4GcumkFlRPpTT2nuhUmWhFqynf/9CSmz0zjPTN38hhgyK9EMhvAL4Y0OewWxTSxXnZvlNQHUN+hkzHqwtApUqN1Jon/TCvxui0EBXi0mmvwYXphyQOEHy6R9N79kiFgxxGaVhPtMmT86KIPJ/V3Frn+iKTIa1A3Du+M0EdLhGzsXI/37AFznlWi8VgZiRh54E2AP8WHkDyPWEFzzOBLHy";
	
	static final String EBAYTOKEN_US = "AgAAAA**AQAAAA**aAAAAA**rVxQWg**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AHlYGmDpCHpQSdj6x9nY+seQ**zNUBAA**AAMAAA**+JsPdLTc8aMhDOmBDgN49Yt6s4OCStflJybzBKvV5ASP7HWYKluhR59l/fcaOG55Prpq/o1hG1hCpJsHKHkwdut8J/B91eFFnIIeIRGfvi0/1NZQgJzhkU2t4Qev0cfb4Y9hhQGNnILN8oznvipVmnTDnLPgXwzVW/tvGmMm/3uuEKkwPUEOAz5e6w/yOuPUBRY6KUC/auJls8IWxuepc4lC3xmrTKWD0PRrtbwVsaBc1Ma0cF9bvXFhksvGUbudAVolfP1HC1mjgxBvPJP4zmnYqE92zuejSqKzs8WBEz1PSoMf/rO9nqicq0OvjrFT+b75USVGi96ey+bg42/6yD2oTs0Dyuwo2e5yrQjgQlpqRDUmIOpGK/a9L8ejkAV/r2Hse56pF1z/qM6+raymzpeu9yO03+JAj9nh7iHhlc65Jbg5wiPyjwFanF2T53ppLlKq4vAXDd+slpsoyqTynMWkEctAIGe48AfEkNNSr6CF3teGZPwR2Ukq3sB/DTZzMk5iX1eNqBdOhTDO54P2f+f6iGYCNBONsHTQ/rY1R3M/0WKk6wA5P1UPU5TC9PxT6xIdBnTDMZltauGf9lnhlOQkARj5Kkjxbz2Ilcnoj5ytDEL2AlQhxPxs9iBYNhzjuxpxEQ0SYJNR92WgTLb6IFKG6ZoDlFJ2pGoAZJMA4VjVLJO1LXAKCas5bnFYjkXBXwRwaTtC36dqP1hYTIPnr0FvnKDCgYJhxHtJzQ70hvgm/VBOgWGxTlfGYZfNkZrZ";
	
	static final String APISERVERURL = "https://api.ebay.com/wsapi";

	static final String DEVID = "b74fdd57-6d7c-4fca-9252-e9fa88257250";
	static final String APPID = "springra-85c3-41bf-af4b-a3802d076c3c";
	static final String CERTID = "54ec499d-9d4d-4069-bf91-6b9197156c24";
	
	static final String DEVID_US = "9e70a91a-07f2-4fe3-a159-9c5d59aab8f7";
	static final String APPID_US = "timxu-inateck-PRD-3bffe2151-3648dad0";
	static final String CERTID_US = "PRD-bffe2151cfe3-1f30-4e74-ad57-b890";
	

	//ebay订单状态  未付款，未发货/已付款，未发货/已付款，已发货/部分发货/未付款，已发货（货到付款）
	static final String NOPAY = "0"; 
	static final String PAY = "1";
	static final String SHIPPED = "2";
	static final String PARTSHIPPED = "3";
	static final String CASHONDELIVERY = "4";
}
