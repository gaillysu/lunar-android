package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.ble.model.notification.CalendarNotification;
import com.medcorp.lunar.ble.model.notification.EmailNotification;
import com.medcorp.lunar.ble.model.notification.FacebookNotification;
import com.medcorp.lunar.ble.model.notification.Notification;
import com.medcorp.lunar.ble.model.notification.SmsNotification;
import com.medcorp.lunar.ble.model.notification.TelephoneNotification;
import com.medcorp.lunar.ble.model.notification.WeChatNotification;
import com.medcorp.lunar.ble.model.notification.WhatsappNotification;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.Map;


/**
 * Created by med on 16/8/1.
 */
public class SetNotificationRequest extends BLERequestData {
    public  final static  byte HEADER = 0x02;
    public static class SetNortificationRequestValues {
        //default vibrator number is 3
        final static byte VIBRATION_ON = 0x03;
        final static byte VIBRATION_OFF = 0x00;
        //motor control bit is bit23
        final static int VIB_MOTOR = 0x800000;
        final static int LED_OFF = 0x000000;
        //color LED control bit is bit16~21
        //white LED control bit is bit0~10

    }

    private byte call_vib_number = 0;
    private int call_led_pattern = 0;

    private byte sms_vib_number = 0;
    private int sms_led_pattern = 0;

    private byte email_vib_number = 0;
    private int email_led_pattern = 0;

    private byte facebook_vib_number = 0;
    private int facebook_led_pattern = 0;

    private byte calendar_vib_number = 0;
    private int calendar_led_pattern = 0;

    private byte wechat_vib_number = 0;
    private int wechat_led_pattern = 0;

    private byte whatsapp_vib_number = 0;
    private int whatsapp_led_pattern = 0;

    public SetNotificationRequest(Context context, Map<Notification, Integer> applicationNotificationColorMap)
    {
        super(new GattAttributesDataSourceImpl(context));
        StateSaver stateSaver = new StateSaver(applicationNotificationColorMap);
        for (Notification applicationNotification: applicationNotificationColorMap.keySet()) {
            applicationNotification.accept(stateSaver);
        }
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {0,HEADER,
                        (byte)(call_vib_number&0xFF),
                        (byte)(call_led_pattern&0xFF),
                        (byte)((call_led_pattern>>8)&0xFF),
                        (byte)((call_led_pattern>>16)&0xFF),

                        (byte)(sms_vib_number&0xFF),
                        (byte)(sms_led_pattern&0xFF),
                        (byte)((sms_led_pattern>>8)&0xFF),
                        (byte)((sms_led_pattern>>16)&0xFF),

                        (byte)(email_vib_number&0xFF),
                        (byte)(email_led_pattern&0xFF),
                        (byte)((email_led_pattern>>8)&0xFF),
                        (byte)((email_led_pattern>>16)&0xFF),

                        (byte)(facebook_vib_number&0xFF),
                        (byte)(facebook_led_pattern&0xFF),
                        (byte)((facebook_led_pattern>>8)&0xFF),
                        (byte)((facebook_led_pattern>>16)&0xFF),

                        (byte)(calendar_vib_number&0xFF),
                        (byte)(calendar_led_pattern&0xFF)
                },

                {(byte) 0xFF,HEADER,
                        (byte)((calendar_led_pattern>>8)&0xFF),
                        (byte)((calendar_led_pattern>>16)&0xFF),
                        (byte)(wechat_vib_number&0xFF),
                        (byte)(wechat_led_pattern&0xFF),
                        (byte)((wechat_led_pattern>>8)&0xFF),
                        (byte)((wechat_led_pattern>>16)&0xFF),
                        (byte)(whatsapp_vib_number&0xFF),
                        (byte)(whatsapp_led_pattern&0xFF),
                        (byte)((whatsapp_led_pattern>>8)&0xFF),
                        (byte)((whatsapp_led_pattern>>16)&0xFF),
                        0,0,0,0,
                        0,0,0,0
                }
        };

    }

    @Override
    public byte getHeader() {
        return HEADER;
    }


    private class StateSaver implements NotificationVisitor<Void> {

        private final Map<Notification, Integer> applicationNotificationColorMap;

        public StateSaver(final Map<Notification, Integer> applicationNotificationColorMap) {
            this.applicationNotificationColorMap = applicationNotificationColorMap;
        }

        @Override
        public Void visit(CalendarNotification calendarNotification) {
            calendar_vib_number = calendarNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            calendar_led_pattern = SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(calendarNotification);
            return null;
        }

        @Override
        public Void visit(EmailNotification emailNotification) {
            email_vib_number= emailNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            email_led_pattern = SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(emailNotification);
            return null;
        }

        @Override
        public Void visit(FacebookNotification facebookNotification) {
            facebook_vib_number = facebookNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            facebook_led_pattern = SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(facebookNotification);
            return null;
        }

        @Override
        public Void visit(SmsNotification smsNotification) {
            sms_vib_number = smsNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            sms_led_pattern= SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(smsNotification);
            return null;
        }

        @Override
        public Void visit(TelephoneNotification telephoneNotification) {
            call_vib_number = telephoneNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            call_led_pattern = SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(telephoneNotification);
            return null;
        }

        @Override
        public Void visit(WeChatNotification weChatNotification) {
            wechat_vib_number = weChatNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            wechat_led_pattern = SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(weChatNotification);
            return null;
        }

        @Override
        public Void visit(WhatsappNotification whatsappNotification) {
            whatsapp_vib_number= whatsappNotification.isOn()? SetNortificationRequestValues.VIBRATION_ON: SetNortificationRequestValues.VIBRATION_OFF;
            whatsapp_led_pattern = SetNortificationRequestValues.VIB_MOTOR | applicationNotificationColorMap.get(whatsappNotification);
            return null;
        }

        @Override
        public Void visit(Notification applicationNotification) {
            return null;
        }
    }
}
