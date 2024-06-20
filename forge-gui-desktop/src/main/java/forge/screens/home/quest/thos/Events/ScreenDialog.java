package forge.screens.home.quest.thos.Events;

import forge.gui.SOverlayUtils;
import forge.gui.UiCommand;
import forge.item.PaperCard;
import forge.localinstance.skin.FSkinProp;
import forge.screens.match.QuestWinLoseCardViewer;
import forge.screens.match.ViewWinLose;
import forge.toolbox.*;
import forge.util.Localizer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static forge.localinstance.properties.ForgeConstants.CACHE_ICON_PICS_DIR;

@SuppressWarnings("WeakerAccess")
/**
 * The UI template for event screens
 */
@Getter @Setter @Accessors(fluent = true, chain = false) public class ScreenDialog {

	String image_key;
	String text;
	HashMap<String, UiCommand> options;
	boolean show_extra;

	@SuppressWarnings("serial")
	public ScreenDialog() {}

	public void init()
	{
		mainPanel.setOpaque(false);
		mainPanel.setBackgroundTexture(FSkin.getIcon(FSkinProp.BG_TEXTURE));

		final FPanel content = new FPanel(new MigLayout("w 40.85sp:40.85sp, hmax 50sp, insets 10 10 10 10, wrap 2"));
		content.setOpaque(false);
		mainPanel.add(content);

//		left.add(new FLabel.Builder().text(localizer.getMessage("lblDistribution")).fontSize(18).build(), "gaptop 10");

		//image
		try {content.add(new JLabel(new ImageIcon(getImage())), "ay top");} catch (Exception e){}

		//text
		FTextPane prompt = new FTextPane(text);
		prompt.setFont(FSkin.getFont(16));
		content.add(prompt, "ay top");

		//extra
		if (show_extra)
		{
			extra.setOpaque(false);
			mainPanel.add(extra);
		}

		//options
		for (Map.Entry<String, UiCommand> option : options.entrySet())
		{
			FButton button = new FButton(option.getKey());
			button.setCommand(option.getValue());
			mainPanel.add(button, "w 40.85sp:40.85sp, h 30px!");
		}
	}

	public void show(final Runnable callback) {
		this.callback = callback;
		final JPanel overlay = FOverlay.SINGLETON_INSTANCE.getPanel();
		overlay.setLayout(new MigLayout("insets 30, gap 15, wrap, ax center, ay center"));
		overlay.add(mainPanel);
//		mainPanel.getRootPane().setDefaultButton(btnOk);
		SOverlayUtils.showOverlay();
	}






	//Helper
	public void push_extra(String msg, final FSkinProp icon) {
		final FSkin.SkinIcon icoTemp = FSkin.getIcon(icon).scale(0.5);

		if (msg.contains("\n")) { /*ensure new line characters are encoded*/ msg = "<html>" + msg.replace("\n", "<br>") + "</html>";}
		final FSkin.SkinnedLabel lblMessage = new FSkin.SkinnedLabel(msg);
		lblMessage.setFont(FSkin.getRelativeFont(14));
		lblMessage.setForeground(ViewWinLose.FORE_COLOR);
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setIconTextGap(50);
		lblMessage.setIcon(icoTemp);

		extra.add(lblMessage, "w 95%!,");
	}

	public void push_cards(final List<PaperCard> cards) {
		final QuestWinLoseCardViewer cv = new QuestWinLoseCardViewer(cards);

		extra.add(cv, "w 95%!, h 330px!, gap 0 0 0 20px");
	}

	public BufferedImage getImage() throws IOException {
		return ImageIO.read(new File(CACHE_ICON_PICS_DIR + image_key));
	}






	//UI declarations

	final Localizer localizer = Localizer.getInstance();

	private final FPanel mainPanel = new FPanel(new MigLayout("w 43sp:43sp, insets 20, gap 6, center, flowy"));
	final FPanel extra = new FPanel(new MigLayout("w 40.85sp:40.85sp, insets 10 10 10 10, flowy"));

	private Runnable callback;

}
