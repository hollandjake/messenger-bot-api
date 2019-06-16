//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hollandjake.messengerBotAPI.util;

public interface XPATHS {
	//Login
	String LOGIN_EMAIL = "//input[@id='email']";
	String LOGIN_PASS = "//input[@id='pass']";
	String LOGIN = "//button[@id='loginbutton']";

	//Signal Elements
	String LOADING_WHEEL = "//i[contains(@class,'_4xu1')]";

	//Inputs
	String INPUT_BOX = "//div[@class='notranslate _5rpu']";

	//Messages
	String ALL_MESSAGES = "//div[contains(@class, 'clearfix _o46 _3erg')]";
	String OTHERS_MESSAGES = ALL_MESSAGES + "[contains(@class,'_29_7')]";
	String MY_MESSAGES = ALL_MESSAGES + "[contains(@class,'_3i_m')]";
	/*Last Message
	(//div[contains(@class, 'clearfix _o46 _3erg')][contains(@class,'_29_7')])[last()]
	 */

	//All Message Types
	/**
	 * RETURNS @data-tooltip-content
	 */
	String MESSAGE_DATE = "./descendant::div[@data-tooltip-content]";

	/**
	 * RETURNS @data-tooltip-content
	 */
	String MESSAGE_SENDER = "./../../div[@class='_1t_q']/div/div[@data-tooltip-content]";

	/**
	 * RETURNS @aria-label
	 */
	String MESSAGE_TEXT = "./div/div[@class='_aok' and @aria-label]";

	//Other Messages
	/**
	 * RETURNS @style
	 */
	String MESSAGE_IMAGE = "./descendant::div[contains(@class,'_4tsk') and @style]";
	/*STICKER

	<div class="clearfix _o46 _3erg _29_7 direction_ltr text_align_ltr">
		<div class="_3058 _15gf">
			//SENT TIME
			<div data-tooltip-content="15:51" data-hover="tooltip" data-tooltip-position="left" class="_3zvs">
				<div aria-label=" Angelic face with halo sticker" class="_2poz _ui9" data-testid="sticker" role="img" tabindex="0" style="background-image: url(&quot;/stickers/asset/?sticker_id=126362197548577&amp;image_type=BestEffortImage&quot;); background-repeat: no-repeat; background-size: 120px 120px; cursor: pointer; height: 120px; width: 120px;">
				</div>
				<span class="_40fu" data-hover="none">
					<span class="_1z_2 _2u_d">
					</span>
				</span>
			</div>
		</div>
	</div>
	 */
	/* TEXT WITH hi @John magic

	<div class="clearfix _o46 _3erg _29_7 direction_ltr text_align_ltr">
		<div data-tooltip-content="16:04" data-hover="tooltip" data-tooltip-position="left" class="_3058 _ui9 _hh7 _6ybn _s1- _52mr _3oh-" id="js_63r" aria-describedby="js_2e">
			<span class="_40fu" data-hover="none">
				<span class="_1z_2 _2u_d">
				</span>
			</span>
			<div class="_aok" tabindex="0" aria-label="Hi @John magic">
				<span class="_3oh- _58nk">
					<span>
						Hi
					</span>
					<div class="_7934 _150g uiPopover _6a _6b">
						<a class="_ih- _p" href="https://www.messenger.com/t/100029054943415" role="button" id="js_5xy" aria-controls="js_6h4" style="color: rgb(0, 132, 255);">
							@John
						</a>
					</div>
					<span>
						 magic
					</span>
				</span>
			</div>
		</div>
	</div>
	 */
	/* IMAGE
	<div class="clearfix _o46 _3erg _29_7 direction_ltr text_align_ltr _ylc" xpath="1">
		<div class="_3058 _15gf">
			<div data-tooltip-content="16:23" data-hover="tooltip" data-tooltip-position="left" class="_3zvs _5z-5" id="js_cjr">
				<div class="_2poz _52mr _ui9 _2n8h _2n8i _5fk1" tabindex="0">
					<div class="_4tsk _52mr _1byr" role="presentation" style="background-image: url(&quot;https://scontent.xx.fbcdn.net/v/t1.15752-0/p280x280/60658531_427628451407355_4413209240307499008_n.jpg?_nc_cat=106&amp;_nc_ad=z-m&amp;_nc_cid=0&amp;_nc_zor=9&amp;_nc_ht=scontent.xx&amp;oh=6af3cdde5753b79ffc7c4110bb8ccdb3&amp;oe=5D910F3D&quot;);">
						<div class="">
						</div>
						<a aria-label="Open photo" class="_4tsl" target="_blank" href="https://l.messenger.com/l.php?u=https%3A%2F%2Fscontent.xx.fbcdn.net%2Fv%2Ft1.15752-0%2Fp280x280%2F60658531_427628451407355_4413209240307499008_n.jpg%3F_nc_cat%3D106%26_nc_ad%3Dz-m%26_nc_cid%3D0%26_nc_zor%3D9%26_nc_ht%3Dscontent.xx%26oh%3D6af3cdde5753b79ffc7c4110bb8ccdb3%26oe%3D5D910F3D&amp;h=AT158gI0ZkmiM_yC0moKYHdoAeEb_w82U01mLO2Pfrz7wxVGCedbLqSNunBv5ssep7ZZ50BTY7P0mFICkxDl-wPa6j47c-Rlb8yPmTJlUnGoIwb-vh3sCag4CnRdJozq8BVfiw" rel="nofollow noopener" data-lynx-mode="hover">
						</a>
						<div style="height: 280px; width: 281px;">
							<img class=" _52mr _1byr _5pf5 img" alt="" src="https://scontent.xx.fbcdn.net/v/t1.15752-0/p280x280/60658531_427628451407355_4413209240307499008_n.jpg?_nc_cat=106&amp;_nc_ad=z-m&amp;_nc_cid=0&amp;_nc_zor=9&amp;_nc_ht=scontent.xx&amp;oh=6af3cdde5753b79ffc7c4110bb8ccdb3&amp;oe=5D910F3D" style="max-width: 100%; width: 100%;">
						</div>
					</div>
				</div>
				<div class="_4kf7 preview">
					<div class="_7ef9">
						<div class="_aou" tabindex="0" role="button">
						</div>
						<div class="_aou _aov">
							<span class="_aow">
								<span data-tooltip-content="React" data-hover="tooltip" data-tooltip-position="above" data-tooltip-alignh="center" class=" _yav">
									<i alt="" class="img sp_sbxFxdCZjuj_1_5x sx_24a2a8">
									</i>
								</span>
							</span>
						</div>
					</div>
				</div>
				<span class="_40fu" data-hover="none">
					<span class="_1z_2 _2u_d">
						<a attachmentids="2404603186490714" attachmenttypes="photo" data-tooltip-content="Forward" data-hover="tooltip" data-tooltip-position="above" data-tooltip-alignh="center" aria-label="Forward" role="button" class="_2t5t" href="#">
						</a>
					</span>
				</span>
			</div>
		</div>
		<span class="_4jzq _jf4 _jf5 _ba3">
			<img class="_jf2 img" alt="Seen by Jake Holland at 16:23" src="https://scontent-lht6-1.xx.fbcdn.net/v/t1.0-1/p80x80/44852288_2070162529709013_74189448299937792_n.jpg?_nc_cat=109&amp;_nc_ht=scontent-lht6-1.xx&amp;oh=82b565570bdd46200dd45200499b36e6&amp;oe=5D81457D" title="Seen by Jake Holland at 16:23">
		</span>
	</div>
	 */
	/*Two image message
	<div class="clearfix _o46 _3erg _29_7 direction_ltr text_align_ltr">
		<div class="_3058 _15gf">
			<div data-tooltip-content="10:45" data-hover="tooltip" data-tooltip-position="left" class="_3zvs _2-x3" id="js_1i5">
				<div class="_2poz _52mr _ui9 _2n8h _4ksk _5fk1" tabindex="0">
					<div class="_2n8g">

						<div class="_4tsk" role="presentation" style="background-image: url(&quot;https://scontent.xx.fbcdn.net/v/t1.15752-0/p280x280/62141724_1066756773712690_4586462143649415168_n.jpg?_nc_cat=108&amp;_nc_ad=z-m&amp;_nc_cid=0&amp;_nc_zor=9&amp;_nc_ht=scontent.xx&amp;oh=c79e2bfa91710cfba81376be78dd55bb&amp;oe=5D95B133&quot;);">
							<div class="">
							</div>
							<a aria-label="Open photo" class="_4tsl" target="_blank" href="https://l.messenger.com/l.php?u=https%3A%2F%2Fscontent.xx.fbcdn.net%2Fv%2Ft1.15752-0%2Fp280x280%2F62141724_1066756773712690_4586462143649415168_n.jpg%3F_nc_cat%3D108%26_nc_ad%3Dz-m%26_nc_cid%3D0%26_nc_zor%3D9%26_nc_ht%3Dscontent.xx%26oh%3Dc79e2bfa91710cfba81376be78dd55bb%26oe%3D5D95B133&amp;h=AT1PIuFQoHfqmgwqSP6cbRXsEihetL6l9IxU2kU_njWFy-XNbJ9WJMvJzEN95oKcXaL1Tb9JZUBDHSZDH_xA5JM-0rGbQLZriReSKChXGc4MRJqOav0WBVswnFlfqVaGZxPqbQ" rel="nofollow noopener" data-lynx-mode="hover">
							</a>
						</div>

						<div class="_4tsk" role="presentation" style="background-image: url(&quot;https://scontent.xx.fbcdn.net/v/t1.15752-0/p280x280/62203622_2309673989288909_8761665504961101824_n.jpg?_nc_cat=103&amp;_nc_ad=z-m&amp;_nc_cid=0&amp;_nc_zor=9&amp;_nc_ht=scontent.xx&amp;oh=c2e6955a637081125c6c65c279af2d86&amp;oe=5D9711CD&quot;);">
							<div class="">
							</div>
							<a aria-label="Open photo" class="_4tsl" target="_blank" href="https://scontent.xx.fbcdn.net/v/t1.15752-0/p280x280/62203622_2309673989288909_8761665504961101824_n.jpg?_nc_cat=103&amp;_nc_ad=z-m&amp;_nc_cid=0&amp;_nc_zor=9&amp;_nc_ht=scontent.xx&amp;oh=c2e6955a637081125c6c65c279af2d86&amp;oe=5D9711CD" rel="nofollow noopener" data-lynx-mode="hover" data-lynx-uri="https://l.messenger.com/l.php?u=https%3A%2F%2Fscontent.xx.fbcdn.net%2Fv%2Ft1.15752-0%2Fp280x280%2F62203622_2309673989288909_8761665504961101824_n.jpg%3F_nc_cat%3D103%26_nc_ad%3Dz-m%26_nc_cid%3D0%26_nc_zor%3D9%26_nc_ht%3Dscontent.xx%26oh%3Dc2e6955a637081125c6c65c279af2d86%26oe%3D5D9711CD&amp;h=AT1PIuFQoHfqmgwqSP6cbRXsEihetL6l9IxU2kU_njWFy-XNbJ9WJMvJzEN95oKcXaL1Tb9JZUBDHSZDH_xA5JM-0rGbQLZriReSKChXGc4MRJqOav0WBVswnFlfqVaGZxPqbQ">
							</a>
						</div>
					</div>
				</div>
				<span class="_40fu" data-hover="none">
					<span class="_1z_2 _2u_d">
					</span>
				</span>
			</div>
		</div>
	</div>
	 */
	/*OLD Message

	<div class="clearfix _o46 _3erg _29_7 direction_ltr text_align_ltr">
		<div data-tooltip-content="21 February 2019 at 21:59" data-hover="tooltip" data-tooltip-position="left" class="_3058 _ui9 _hh7 _6ybn _s1- _52mr _3oh-">
			<span class="_40fu" data-hover="none">
				<span class="_1z_2 _2u_d">
				</span>
			</span>
			<div class="_aok" tabindex="0" aria-label="@John Smith">
				<span class="_3oh- _58nk">
					<div class="_7934 _150g uiPopover _6a _6b">
						<a class="_ih- _p" href="https://www.messenger.com/t/100029054943415" role="button" id="js_3bh" aria-controls="js_3bi" style="color: rgb(0, 132, 255);">
							@John Smith
						</a>
					</div>
				</span>
			</div>
		</div>
	</div>
	 */

	static String LAST_MINUS_N(String query, int n) {
		return "(" + query + ")[last()-" + n + "]";
	}

	static String LAST(String query) {
		return LAST_MINUS_N(query, 0);
	}
}