package eu.siacs.conversations.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Pair;
import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.siacs.conversations.Config;
import eu.siacs.conversations.R;
import eu.siacs.conversations.crypto.axolotl.AxolotlService;
import eu.siacs.conversations.entities.Contact;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.entities.ListItem;
import eu.siacs.conversations.entities.Message;
import eu.siacs.conversations.entities.MucOptions;
import eu.siacs.conversations.entities.Presence;
import eu.siacs.conversations.entities.Transferable;
import eu.siacs.conversations.xmpp.jid.Jid;

public class UIHelper {

	private static int[] UNSAFE_COLORS = {
			0xFFF44336, //red 500
			0xFFE53935, //red 600
			0xFFD32F2F, //red 700
			0xFFC62828, //red 800

			0xFFEF6C00, //orange 800

			0xFFF4511E, //deep orange 600
			0xFFE64A19, //deep orange 700
			0xFFD84315, //deep orange 800,
	};

	private static int[] SAFE_COLORS = {
			0xFFE91E63, //pink 500
			0xFFD81B60, //pink 600
			0xFFC2185B, //pink 700
			0xFFAD1457, //pink 800

			0xFF9C27B0, //purple 500
			0xFF8E24AA, //purple 600
			0xFF7B1FA2, //purple 700
			0xFF6A1B9A, //purple 800

			0xFF673AB7, //deep purple 500,
			0xFF5E35B1, //deep purple 600
			0xFF512DA8, //deep purple 700
			0xFF4527A0, //deep purple 800,

			0xFF3F51B5, //indigo 500,
			0xFF3949AB,//indigo 600
			0xFF303F9F,//indigo 700
			0xFF283593, //indigo 800

			0xFF2196F3, //blue 500
			0xFF1E88E5, //blue 600
			0xFF1976D2, //blue 700
			0xFF1565C0, //blue 800

			0xFF03A9F4, //light blue 500
			0xFF039BE5, //light blue 600
			0xFF0288D1, //light blue 700
			0xFF0277BD, //light blue 800

			0xFF00BCD4, //cyan 500
			0xFF00ACC1, //cyan 600
			0xFF0097A7, //cyan 700
			0xFF00838F, //cyan 800

			0xFF009688, //teal 500,
			0xFF00897B, //teal 600
			0xFF00796B, //teal 700
			0xFF00695C, //teal 800,

			//0xFF558B2F, //light green 800

			//0xFFC0CA33, //lime 600
			0xFF9E9D24, //lime 800

			0xFF795548, //brown 500,
			//0xFF4E342E, //brown 800
			0xFF607D8B, //blue grey 500,
			//0xFF37474F //blue grey 800
	};

	private static final int[] COLORS;

	static {
		COLORS = Arrays.copyOf(SAFE_COLORS, SAFE_COLORS.length + UNSAFE_COLORS.length);
		System.arraycopy(UNSAFE_COLORS, 0, COLORS, SAFE_COLORS.length, UNSAFE_COLORS.length);
	}

	private static final List<String> LOCATION_QUESTIONS = Arrays.asList(
			"where are you", //en
			"where are you now", //en
			"where are you right now", //en
			"whats your 20", //en
			"what is your 20", //en
			"what's your 20", //en
			"whats your twenty", //en
			"what is your twenty", //en
			"what's your twenty", //en
			"wo bist du", //de
			"wo bist du jetzt", //de
			"wo bist du gerade", //de
			"wo seid ihr", //de
			"wo seid ihr jetzt", //de
			"wo seid ihr gerade", //de
			"d??nde est??s", //es
			"donde estas" //es
	);

	private static final List<Character> PUNCTIONATION = Arrays.asList('.', ',', '?', '!', ';', ':');

	private static final int SHORT_DATE_FLAGS = DateUtils.FORMAT_SHOW_DATE
			| DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_ALL;
	private static final int FULL_DATE_FLAGS = DateUtils.FORMAT_SHOW_TIME
			| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_DATE;

	public static String readableTimeDifference(Context context, long time) {
		return readableTimeDifference(context, time, false);
	}

	public static String readableTimeDifferenceFull(Context context, long time) {
		return readableTimeDifference(context, time, true);
	}

	private static String readableTimeDifference(Context context, long time,
	                                             boolean fullDate) {
		if (time == 0) {
			return context.getString(R.string.just_now);
		}
		Date date = new Date(time);
		long difference = (System.currentTimeMillis() - time) / 1000;
		if (difference < 60) {
			return context.getString(R.string.just_now);
		} else if (difference < 60 * 2) {
			return context.getString(R.string.minute_ago);
		} else if (difference < 60 * 15) {
			return context.getString(R.string.minutes_ago, Math.round(difference / 60.0));
		} else if (today(date)) {
			java.text.DateFormat df = DateFormat.getTimeFormat(context);
			return df.format(date);
		} else {
			if (fullDate) {
				return DateUtils.formatDateTime(context, date.getTime(),
						FULL_DATE_FLAGS);
			} else {
				return DateUtils.formatDateTime(context, date.getTime(),
						SHORT_DATE_FLAGS);
			}
		}
	}

	private static boolean today(Date date) {
		return sameDay(date, new Date(System.currentTimeMillis()));
	}

	public static boolean today(long date) {
		return sameDay(date, System.currentTimeMillis());
	}

	public static boolean yesterday(long date) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.DAY_OF_YEAR, -1);
		cal2.setTime(new Date(date));
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean sameDay(long a, long b) {
		return sameDay(new Date(a), new Date(b));
	}

	private static boolean sameDay(Date a, Date b) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(a);
		cal2.setTime(b);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR);
	}

	public static String lastseen(Context context, boolean active, long time) {
		long difference = (System.currentTimeMillis() - time) / 1000;
		if (active) {
			return context.getString(R.string.online_right_now);
		} else if (difference < 60) {
			return context.getString(R.string.last_seen_now);
		} else if (difference < 60 * 2) {
			return context.getString(R.string.last_seen_min);
		} else if (difference < 60 * 60) {
			return context.getString(R.string.last_seen_mins, Math.round(difference / 60.0));
		} else if (difference < 60 * 60 * 2) {
			return context.getString(R.string.last_seen_hour);
		} else if (difference < 60 * 60 * 24) {
			return context.getString(R.string.last_seen_hours,
					Math.round(difference / (60.0 * 60.0)));
		} else if (difference < 60 * 60 * 48) {
			return context.getString(R.string.last_seen_day);
		} else {
			return context.getString(R.string.last_seen_days,
					Math.round(difference / (60.0 * 60.0 * 24.0)));
		}
	}

	public static int getColorForName(String name) {
		return getColorForName(name, false);
	}

	public static int getColorForName(String name, boolean safe) {
		if (name == null || name.isEmpty()) {
			return 0xFF202020;
		}
		if (safe) {
			return SAFE_COLORS[(int) (getLongForName(name) % SAFE_COLORS.length)];
		} else {
			return COLORS[(int) (getLongForName(name) % COLORS.length)];
		}
	}

	private static long getLongForName(String name) {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			return Math.abs(new BigInteger(messageDigest.digest(name.getBytes())).longValue());
		} catch (Exception e) {
			return 0;
		}
	}

	public static Pair<String, Boolean> getMessagePreview(final Context context, final Message message) {
		final Transferable d = message.getTransferable();
		if (d != null) {
			switch (d.getStatus()) {
				case Transferable.STATUS_CHECKING:
					return new Pair<>(context.getString(R.string.checking_x,
							getFileDescriptionString(context, message)), true);
				case Transferable.STATUS_DOWNLOADING:
					return new Pair<>(context.getString(R.string.receiving_x_file,
							getFileDescriptionString(context, message),
							d.getProgress()), true);
				case Transferable.STATUS_OFFER:
				case Transferable.STATUS_OFFER_CHECK_FILESIZE:
					return new Pair<>(context.getString(R.string.x_file_offered_for_download,
							getFileDescriptionString(context, message)), true);
				case Transferable.STATUS_DELETED:
					return new Pair<>(context.getString(R.string.file_deleted), true);
				case Transferable.STATUS_FAILED:
					return new Pair<>(context.getString(R.string.file_transmission_failed), true);
				case Transferable.STATUS_UPLOADING:
					if (message.getStatus() == Message.STATUS_OFFERED) {
						return new Pair<>(context.getString(R.string.offering_x_file,
								getFileDescriptionString(context, message)), true);
					} else {
						return new Pair<>(context.getString(R.string.sending_x_file,
								getFileDescriptionString(context, message)), true);
					}
				default:
					return new Pair<>("", false);
			}
		} else if (message.getEncryption() == Message.ENCRYPTION_PGP) {
			return new Pair<>(context.getString(R.string.pgp_message), true);
		} else if (message.getEncryption() == Message.ENCRYPTION_DECRYPTION_FAILED) {
			return new Pair<>(context.getString(R.string.decryption_failed), true);
		} else if (message.getType() == Message.TYPE_FILE || message.getType() == Message.TYPE_IMAGE) {
			if (message.getStatus() == Message.STATUS_RECEIVED) {
				return new Pair<>(context.getString(R.string.received_x_file,
						getFileDescriptionString(context, message)), true);
			} else {
				return new Pair<>(getFileDescriptionString(context, message), true);
			}
		} else {
			final String body = message.getBody();
			if (body.startsWith(Message.ME_COMMAND)) {
				return new Pair<>(body.replaceAll("^" + Message.ME_COMMAND,
						UIHelper.getMessageDisplayName(message) + " "), false);
			} else if (message.isGeoUri()) {
				if (message.getStatus() == Message.STATUS_RECEIVED) {
					return new Pair<>(context.getString(R.string.received_location), true);
				} else {
					return new Pair<>(context.getString(R.string.location), true);
				}
			} else if (message.treatAsDownloadable()) {
				return new Pair<>(context.getString(R.string.x_file_offered_for_download,
						getFileDescriptionString(context, message)), true);
			} else {
				String[] lines = body.split("\n");
				StringBuilder builder = new StringBuilder();
				for (String l : lines) {
					if (l.length() > 0) {
						char first = l.charAt(0);
						if ((first != '>' || !isPositionFollowedByQuoteableCharacter(l, 0)) && first != '\u00bb') {
							String line = l.trim();
							if (line.isEmpty()) {
								continue;
							}
							char last = line.charAt(line.length() - 1);
							if (builder.length() != 0) {
								builder.append(' ');
							}
							builder.append(line);
							if (!PUNCTIONATION.contains(last)) {
								break;
							}
						}
					}
				}
				if (builder.length() == 0) {
					builder.append(body.trim());
				}
				return new Pair<>(builder.length() > 256 ? builder.substring(0, 256) : builder.toString(), false);
			}
		}
	}

	public static boolean isPositionFollowedByQuoteableCharacter(CharSequence body, int pos) {
		return !isPositionFollowedByNumber(body, pos)
				&& !isPositionFollowedByEmoticon(body, pos)
				&& !isPositionFollowedByEquals(body, pos);
	}

	private static boolean isPositionFollowedByNumber(CharSequence body, int pos) {
		boolean previousWasNumber = false;
		for (int i = pos + 1; i < body.length(); i++) {
			char c = body.charAt(i);
			if (Character.isDigit(body.charAt(i))) {
				previousWasNumber = true;
			} else if (previousWasNumber && (c == '.' || c == ',')) {
				previousWasNumber = false;
			} else {
				return (Character.isWhitespace(c) || c == '%' || c == '+') && previousWasNumber;
			}
		}
		return previousWasNumber;
	}

	private static boolean isPositionFollowedByEquals(CharSequence body, int pos) {
		return body.length() > pos + 1 && body.charAt(pos + 1) == '=';
	}

	private static boolean isPositionFollowedByEmoticon(CharSequence body, int pos) {
		if (body.length() <= pos + 1) {
			return false;
		} else {
			final char first = body.charAt(pos + 1);
			return first == ';'
					|| first == ':'
					|| closingBeforeWhitespace(body, pos + 1);
		}
	}

	private static boolean closingBeforeWhitespace(CharSequence body, int pos) {
		for (int i = pos; i < body.length(); ++i) {
			final char c = body.charAt(i);
			if (Character.isWhitespace(c)) {
				return false;
			} else if (c == '<' || c == '>') {
				return body.length() == i + 1 || Character.isWhitespace(body.charAt(i + 1));
			}
		}
		return false;
	}

	public static boolean isPositionFollowedByQuote(CharSequence body, int pos) {
		if (body.length() <= pos + 1 || Character.isWhitespace(body.charAt(pos + 1))) {
			return false;
		}
		boolean previousWasWhitespace = false;
		for (int i = pos + 1; i < body.length(); i++) {
			char c = body.charAt(i);
			if (c == '\n' || c == '??') {
				return false;
			} else if (c == '??' && !previousWasWhitespace) {
				return true;
			} else {
				previousWasWhitespace = Character.isWhitespace(c);
			}
		}
		return false;
	}

	public static String getDisplayName(MucOptions.User user) {
		Contact contact = user.getContact();
		if (contact != null) {
			return contact.getDisplayName();
		} else {
			final String name = user.getName();
			if (name != null) {
				return name;
			}
			final Jid realJid = user.getRealJid();
			if (realJid != null) {
				return JidHelper.localPartOrFallback(realJid);
			}
			return null;
		}
	}

	public static String concatNames(List<MucOptions.User> users) {
		return concatNames(users, users.size());
	}

	public static String concatNames(List<MucOptions.User> users, int max) {
		StringBuilder builder = new StringBuilder();
		final boolean shortNames = users.size() >= 3;
		for (int i = 0; i < Math.max(users.size(), max); ++i) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			final String name = UIHelper.getDisplayName(users.get(i));
			builder.append(shortNames ? name.split("\\s+")[0] : name);
		}
		return builder.toString();
	}

	public static String getFileDescriptionString(final Context context, final Message message) {
		if (message.getType() == Message.TYPE_IMAGE) {
			return context.getString(R.string.image);
		}
		final String mime = message.getMimeType();
		if (mime == null) {
			return context.getString(R.string.file);
		} else if (mime.startsWith("audio/")) {
			return context.getString(R.string.audio);
		} else if (mime.startsWith("video/")) {
			return context.getString(R.string.video);
		} else if (mime.startsWith("image/")) {
			return context.getString(R.string.image);
		} else if (mime.contains("pdf")) {
			return context.getString(R.string.pdf_document);
		} else if (mime.contains("application/vnd.android.package-archive")) {
			return context.getString(R.string.apk);
		} else if (mime.contains("vcard")) {
			return context.getString(R.string.vcard);
		} else {
			return mime;
		}
	}

	public static String getMessageDisplayName(final Message message) {
		final Conversation conversation = message.getConversation();
		if (message.getStatus() == Message.STATUS_RECEIVED) {
			final Contact contact = message.getContact();
			if (conversation.getMode() == Conversation.MODE_MULTI) {
				if (contact != null) {
					return contact.getDisplayName();
				} else {
					return getDisplayedMucCounterpart(message.getCounterpart());
				}
			} else {
				return contact != null ? contact.getDisplayName() : "";
			}
		} else {
			if (conversation.getMode() == Conversation.MODE_MULTI) {
				return conversation.getMucOptions().getSelf().getName();
			} else {
				final Jid jid = conversation.getAccount().getJid();
				return jid.hasLocalpart() ? jid.getLocalpart() : jid.toDomainJid().toString();
			}
		}
	}

	public static String getMessageHint(Context context, Conversation conversation) {
		switch (conversation.getNextEncryption()) {
			case Message.ENCRYPTION_NONE:
				if (Config.multipleEncryptionChoices()) {
					return context.getString(R.string.send_unencrypted_message);
				} else {
					return context.getString(R.string.send_message_to_x, conversation.getName());
				}
			case Message.ENCRYPTION_OTR:
				return context.getString(R.string.send_otr_message);
			case Message.ENCRYPTION_AXOLOTL:
				AxolotlService axolotlService = conversation.getAccount().getAxolotlService();
				if (axolotlService != null && axolotlService.trustedSessionVerified(conversation)) {
					return context.getString(R.string.send_omemo_x509_message);
				} else {
					return context.getString(R.string.send_omemo_message);
				}
			case Message.ENCRYPTION_PGP:
				return context.getString(R.string.send_pgp_message);
			default:
				return "";
		}
	}

	public static String getDisplayedMucCounterpart(final Jid counterpart) {
		if (counterpart == null) {
			return "";
		} else if (!counterpart.isBareJid()) {
			return counterpart.getResourcepart().trim();
		} else {
			return counterpart.toString().trim();
		}
	}

	public static boolean receivedLocationQuestion(Message message) {
		if (message == null
				|| message.getStatus() != Message.STATUS_RECEIVED
				|| message.getType() != Message.TYPE_TEXT) {
			return false;
		}
		String body = message.getBody() == null ? null : message.getBody().trim().toLowerCase(Locale.getDefault());
		body = body.replace("?", "").replace("??", "");
		return LOCATION_QUESTIONS.contains(body);
	}

	public static ListItem.Tag getTagForStatus(Context context, Presence.Status status) {
		switch (status) {
			case CHAT:
				return new ListItem.Tag(context.getString(R.string.presence_chat), 0xff259b24);
			case AWAY:
				return new ListItem.Tag(context.getString(R.string.presence_away), 0xffff9800);
			case XA:
				return new ListItem.Tag(context.getString(R.string.presence_xa), 0xfff44336);
			case DND:
				return new ListItem.Tag(context.getString(R.string.presence_dnd), 0xfff44336);
			default:
				return new ListItem.Tag(context.getString(R.string.presence_online), 0xff259b24);
		}
	}

	public static String tranlasteType(Context context, String type) {
		switch (type.toLowerCase()) {
			case "pc":
				return context.getString(R.string.type_pc);
			case "phone":
				return context.getString(R.string.type_phone);
			case "tablet":
				return context.getString(R.string.type_tablet);
			case "web":
				return context.getString(R.string.type_web);
			case "console":
				return context.getString(R.string.type_console);
			default:
				return type;
		}
	}

	public static boolean showIconsInPopup(PopupMenu attachFilePopup) {
		try {
			Field field = attachFilePopup.getClass().getDeclaredField("mPopup");
			field.setAccessible(true);
			Object menuPopupHelper = field.get(attachFilePopup);
			Class<?> cls = Class.forName("com.android.internal.view.menu.MenuPopupHelper");
			Method method = cls.getDeclaredMethod("setForceShowIcon", new Class[]{boolean.class});
			method.setAccessible(true);
			method.invoke(menuPopupHelper, new Object[]{true});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
